package deus.atoms;

import deus.atoms.mixin.I18nAccessor;
import deus.atoms.mixin.LanguageAccessor;
import deus.atoms.toml.AtomDataCache;
import deus.atoms.toml.AtomLoader;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "atoms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<Block<?>> blocks = new ArrayList<>();
	public static final List<Item> items = new ArrayList<>();

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization started.");

		if (!loadAtomDefinitions()) {
			return;
		}

		Map<AtomType, List<?>> atoms = AtomDataCache.ATOMS;
		if (atoms.isEmpty()) {
			LOGGER.error("No atoms found during initialization!");
			return;
		}

		AtomLoader.loadAtoms(atoms);
		AtomLoader.loadTextures(atoms);
	}

	@Override
	public void afterGameStart() {
		registerLanguageEntries();
	}

	private boolean loadAtomDefinitions() {
		try {
			AtomLoader.createFolders();
			AtomDataCache.ATOMS = AtomLoader.loadAllAtoms();
			LOGGER.info("Atom definitions successfully loaded.");
			return true;
		} catch (IOException e) {
			LOGGER.error("Failed to load atom definitions.", e);
			return false;
		}
	}

	private void registerLanguageEntries() {
		LOGGER.info("Registering language entries for atoms...");

		List<CompiledBlock> blockAtoms = getBlockAtoms();
		if (blockAtoms == null) {
			return;
		}

		Language language = getLanguage();
		LanguageAccessor langAccessor = (LanguageAccessor) language;
		String localeId = language.getId();

		blockAtoms.forEach(atom -> registerAtomLanguage(atom, langAccessor, localeId));
	}

	@SuppressWarnings("unchecked")
	private List<CompiledBlock> getBlockAtoms() {
		return (List<CompiledBlock>) AtomDataCache.ATOMS.get(AtomType.BLOCK);
	}

	private Language getLanguage() {
		return ((I18nAccessor) I18n.getInstance()).getLanguage();
	}

	private void registerAtomLanguage(CompiledBlock atom, LanguageAccessor langAccessor, String localeId) {
		if (!isValidAtomForLanguage(atom)) {
			return;
		}

		CompiledBlock.Lang.LangLocale localeTable = atom.lang.locales.get(localeId);
		if (localeTable == null) {
			LOGGER.warn("Atom '{}' has no entries for locale '{}'.", atom.lang.key, localeId);
			return;
		}

		registerLanguageKeys(atom.lang.key, localeTable, langAccessor);
		logRegisteredEntries(atom.lang.key, localeTable);
	}

	private boolean isValidAtomForLanguage(CompiledBlock atom) {
		if (atom.lang == null) {
			LOGGER.warn("Atom has no 'lang' section. Skipping.");
			return false;
		}

		if (atom.lang.key == null) {
			LOGGER.warn("Atom missing langkey. Skipping language registration.");
			return false;
		}

		return true;
	}

	private void registerLanguageKeys(String key, CompiledBlock.Lang.LangLocale locale, LanguageAccessor langAccessor) {
		if (locale.name != null) {
			langAccessor.getEntries().put("tile.atoms." + key + ".name", locale.name);
		}
		if (locale.desc != null) {
			langAccessor.getEntries().put("tile.atoms." + key + ".desc", locale.desc);
		}
		if (locale.tooltip != null) {
			langAccessor.getEntries().put("tile.atoms." + key + ".tooltip", locale.tooltip);
		}
	}

	private void logRegisteredEntries(String key, CompiledBlock.Lang.LangLocale locale) {
		LOGGER.info(
			"Loaded language entries for '{}': name='{}', desc='{}', tooltip='{}'",
			key, locale.name, locale.desc, locale.tooltip
		);
	}
}
