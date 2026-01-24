package deus.btd.pipeline.registry;

import deus.btd.Main;
import deus.btd.interfaces.IHasLang;
import deus.btd.interfaces.LanguageEntrypoint;
import deus.btd.mixin.I18nAccessor;
import deus.btd.mixin.LanguageAccessor;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
import deus.btd.pipeline.compile.types.fields.Lang;
import deus.btd.pipeline.project.ProjectProcessed;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static deus.btd.pipeline.registry.AtomProjectRegistry.PROJECTS;

public class LanguageRegistry implements LanguageEntrypoint {

	@Override
	public void register(Language language) {
		Main.LOGGER.info("Registering language entries for atoms...");

		for (ProjectProcessed project : PROJECTS) {
			List<IHasLang> atoms = getAtomsOfType(project.atoms.values(), IHasLang.class);
			if (atoms == null || atoms.isEmpty()) {
				continue;
			}

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
			Main.LOGGER.warn("Atom '{}' has no entries for locale '{}'.", atom.getLang().key, localeId);
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
			Main.LOGGER.warn("Atom has no 'lang' section. Skipping.");
			return false;
		}

		if (atom.getLang().key == null) {
			Main.LOGGER.warn("Atom missing langkey. Skipping language registration.");
			return false;
		}

		return true;
	}


	private void logRegisteredEntries(String key, Lang.LangLocale locale) {
		Main.LOGGER.info(
			"Loaded language entries for '{}': name='{}', desc='{}', tooltip='{}'",
			key, locale.name, locale.desc, locale.tooltip
		);
	}
}
