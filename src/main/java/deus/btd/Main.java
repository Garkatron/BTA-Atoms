package deus.btd;

import deus.btd.interfaces.IHasLang;
import deus.btd.mixin.I18nAccessor;
import deus.btd.mixin.LanguageAccessor;
import deus.btd.pipeline.io.AtomPaths;
import deus.btd.pipeline.io.AtomProjectLoader;
import deus.btd.pipeline.project.AtomDataCache;
import deus.btd.pipeline.project.ProjectProcessed;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
import deus.btd.pipeline.compile.types.fields.Lang;
import deus.btd.pipeline.project.ProjectProcessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "better_than_datapacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<Block<?>> blocks = new ArrayList<>();
	public static List<ProjectProcessed> PROJECTS;

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization started.");

		try {
			AtomPaths.createFolders();

			LOGGER.info("Creating folders.");
		} catch (IOException e) {
			LOGGER.error("Error creating folders.");
			throw new RuntimeException(e);
		}

		LOGGER.info("Processing projects.");
		AtomPaths.deletePreviousConfig();
		PROJECTS = ProjectProcessor.processProjects(AtomProjectLoader.loadProjects(AtomPaths.DATA));


	}

	@Override
	public void afterGameStart() {
		LOGGER.info("Registering language entries.");

		for (ProjectProcessed project : PROJECTS) {
			project.resources.loadTextures();
			project.resources.loadDatapack();
		}
		Minecraft.getMinecraft().texturePackList.refresh();
		registerLanguageEntries();

	}

	private void registerLanguageEntries() {
		LOGGER.info("Registering language entries for atoms...");

		for (ProjectProcessed project : PROJECTS) {
			List<IHasLang> atoms = getAtomsOfType(project.resources.getAtoms().values(), IHasLang.class);
			if (atoms == null || atoms.isEmpty()) {
				continue;
			}

			Language language = getLanguage();
			LanguageAccessor langAccessor = (LanguageAccessor) language;
			String localeId = language.getId();

			atoms.forEach(atom -> registerAtomLanguage(atom, langAccessor, localeId));
		}
	}


	private <T> List<T> getAtomsOfType(Collection<List<?>> atomLists, Class<T> type) {
		List<T> result = new ArrayList<>();

		for (List<?> atomList : atomLists) {
			if (atomList == null) continue;

			for (Object atom : atomList) {
				if (type.isInstance(atom)) {
					result.add(type.cast(atom));
				}
			}
		}

		return result.isEmpty() ? null : result;
	}


	private Language getLanguage() {
		return ((I18nAccessor) I18n.getInstance()).getLanguage();
	}

	private void registerAtomLanguage(IHasLang atom, LanguageAccessor langAccessor, String localeId) {
		if (!isValidAtomForLanguage(atom)) {
			return;
		}

		Lang.LangLocale localeTable = atom.getLang().locales.get(localeId);
		if (localeTable == null) {
			LOGGER.warn("Atom '{}' has no entries for locale '{}'.", atom.getLang().key, localeId);
			return;
		}

		String prefix = getAtomPrefix(atom);

		registerLanguageKeys(atom.Namespace(), atom.getLang().key, localeTable, langAccessor, prefix);
		logRegisteredEntries(atom.getLang().key, localeTable);
	}

	private String getAtomPrefix(IHasLang atom) {
		if (atom instanceof CompiledBlock) {
			return "tile";
		} else if (atom instanceof CompiledItem) {
			return "item";
		}
		return "tile";
	}

	private void registerLanguageKeys(String namespace, String key, Lang.LangLocale locale, LanguageAccessor langAccessor, String prefix) {
		String baseKey;
		if (prefix.equals("tile")) {
			baseKey = "tile." + namespace +  "." + key;
		} else {
			baseKey = "item." + key;
		}

		if (locale.name != null) {
			langAccessor.getEntries().put(baseKey + ".name", locale.name);
		}
		if (locale.desc != null) {
			langAccessor.getEntries().put(baseKey + ".desc", locale.desc);
		}
		if (locale.tooltip != null) {
			langAccessor.getEntries().put(baseKey + ".tooltip", locale.tooltip);
		}
	}

	private boolean isValidAtomForLanguage(IHasLang atom) {
		if (atom.getLang() == null) {
			LOGGER.warn("Atom has no 'lang' section. Skipping.");
			return false;
		}

		if (atom.getLang().key == null) {
			LOGGER.warn("Atom missing langkey. Skipping language registration.");
			return false;
		}

		return true;
	}


	private void logRegisteredEntries(String key, Lang.LangLocale locale) {
		LOGGER.info(
			"Loaded language entries for '{}': name='{}', desc='{}', tooltip='{}'",
			key, locale.name, locale.desc, locale.tooltip
		);
	}
}
