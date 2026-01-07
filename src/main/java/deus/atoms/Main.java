package deus.atoms;

import deus.atoms.mixin.AtlasStitcherAccessor;
import deus.atoms.mixin.I18nAccessor;
import deus.atoms.mixin.LanguageAccessor;
import deus.atoms.utils.CompiledBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static deus.atoms.ConfigManager.configBlockIDsFromNames;



public class Main implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "atoms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<Block<?>> blocks = new ArrayList<>();


	@Override
	public void beforeGameStart() {
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization started.");

		// TRY LOAD ATOMS
		try {
			AtomLoader.createFolders();
			AtomDataCache.ATOMS = AtomLoader.loadAllAtoms();
			LOGGER.info("Atom definitions successfully loaded.");
		} catch (IOException e) {
			LOGGER.error("Failed to load atom definitions.", e);
			return;
		}

		List<CompiledBlock> atoms = AtomDataCache.ATOMS;
		if (atoms.isEmpty()) {
			LOGGER.error("No atoms found during initialization!");
			return;
		}

		loadAtoms(atoms);
		loadTextures(atoms);



	}

	private void loadTextures(List<CompiledBlock> atoms) {
		// PROCESSING ALL
		LOGGER.info("Registering textures for {} atoms...", atoms.size());

		for (CompiledBlock atom : atoms) {

			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) continue;

			// IF NOT NAME AVOID IT
			if (atom.data.name == null) {
				LOGGER.warn("Encountered atom without a name. Skipping texture registration.");
				continue;
			}

			// IF NOT TEXTURES AVOID IT
			if (atom.textures == null) {
				LOGGER.warn("Atom '{}' has no textures table. Skipping.", atom.data.name);
				continue;
			}

			boolean isBase64 = atom.textures.encoding.equals("base64");

			// Iterate over all entries in the table
			String[] faces = new String[] {
				"top", "bottom", "north", "south", "west", "east"
			};

			for (String face : faces) {
				String tex = null;

				switch (face) {
					case "top":    tex = atom.textures.faces.top; break;
					case "bottom": tex = atom.textures.faces.bottom; break;
					case "north":  tex = atom.textures.faces.north; break;
					case "south":  tex = atom.textures.faces.south; break;
					case "west":   tex = atom.textures.faces.west; break;
					case "east":   tex = atom.textures.faces.east; break;
				}

				if (face == null) {
					LOGGER.warn("Texture for face '{}' in atom '{}' is null. Skipping.", face, atom.data.name);
					continue;
				}

				// CREATING TEXTURE INTO ATOMS TEXTURES FOLDER
				String texPath;
				if (isBase64) {
					LOGGER.info("Texture '{}' for atom '{}' is base64.", face, atom.data.name);
					String relativePath = AtomLoader.loadB64PNG(tex, atom.data.name, face);
					texPath = relativePath;
				} else {
					LOGGER.info("Texture '{}' for atom '{}' is a path.", face, atom.data.name);
					texPath = face;
				}

				// REGISTER THE TEXTURE
				NamespaceID id = NamespaceID.getPermanent(MOD_ID, texPath);
				LOGGER.info("Registering texture '{}' for atom '{}'.", id, atom.data.name);

				((AtlasStitcherAccessor) TextureRegistry.blockAtlas).callGetTexture(id);
			}

		}

		// READING BLOCK FIELDS
		LOGGER.info("Assigning block IDs from config...");

	}

	private void loadAtoms(List<CompiledBlock> atoms) {
		try {

			// CREATE ATOMS MOD CONFIG WITH ALL LOADED BLOCKS NAMES
			List<String> keys = atoms.stream()
				.map(atom -> {
					String author = atom.meta != null && atom.meta.author != null ? atom.meta.author : "Unnamed";
					String name = atom.data != null && atom.data.name != null ? atom.data.name : "UnnamedBlock";
					return author + "_" + name;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledBlock atom : atoms) {
				AtomCompiler.convertoIntoBlocks(atom);
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterGameStart() {

		LOGGER.info("Registering language entries for atoms...");

		List<CompiledBlock> atoms = AtomDataCache.ATOMS;
		Language language = ((I18nAccessor) I18n.getInstance()).getLanguage();
		LanguageAccessor lang = (LanguageAccessor) language;

		atoms.forEach(atom -> {
			if (atom.lang.key == null) {
				LOGGER.warn("Atom missing langkey. Skipping language registration.");
				return;
			}

			String langid = language.getId();

			if (atom.lang == null) {
				LOGGER.warn("Atom '{}' has no 'lang' section. Skipping.", atom.lang.key);
				return;
			}

			CompiledBlock.Lang.LangLocale localeTable = atom.lang.locales.get(langid);
			if (localeTable == null) {
				LOGGER.warn("Atom '{}' has no entries for locale '{}'.", atom.lang.key, langid);
				return;
			}

			String displayName = localeTable.name;
			String description = localeTable.desc;
			String tooltip = localeTable.tooltip;

			if (displayName != null)
				lang.getEntries().put("tile.atoms." + atom.lang.key + ".name", displayName);
			if (description != null)
				lang.getEntries().put("tile.atoms." + atom.lang.key + ".desc", description);
			if (tooltip != null)
				lang.getEntries().put("tile.atoms." + atom.lang.key + ".tooltip", tooltip);

			LOGGER.info(
				"Loaded language entries for '{}': name='{}', desc='{}', tooltip='{}'",
				atom.lang.key, displayName, description, tooltip
			);
		});

	}


}
