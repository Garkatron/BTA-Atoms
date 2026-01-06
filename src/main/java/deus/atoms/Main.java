package deus.atoms;

import deus.atoms.blocks.AtomBlockLogic;
import deus.atoms.gui.LoadingProgressBar;
import deus.atoms.mixin.AtlasStitcherAccessor;
import deus.atoms.mixin.I18nAccessor;
import deus.atoms.mixin.LanguageAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static deus.atoms.ConfigManager.blockGoc;
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

		List<TomlParseResult> atoms = AtomDataCache.ATOMS;
		if (atoms.isEmpty()) {
			LOGGER.error("No atoms found during initialization!");
			return;
		}

		loadTextures(atoms);
		loadAtoms(atoms);

	}

	private void loadTextures(List<TomlParseResult> atoms) {
		// PROCESSING ALL
		LOGGER.info("Registering textures for {} atoms...", atoms.size());

		for (TomlParseResult atom : atoms) {

			int version = AtomCompiler.getOrDefault(atom, "meta.format_version", 0);
			if (version != AtomCompiler.AtomFormatVersion) continue;

			// IF NOT NAME AVOID IT
			String atomName = atom.getString("data.name");
			if (atomName == null) {
				LOGGER.warn("Encountered atom without a name. Skipping texture registration.");
				continue;
			}

			// IF NOT TEXTURES AVOID IT
			TomlTable texturesTable = atom.getTable("textures.faces");
			if (texturesTable == null) {
				LOGGER.warn("Atom '{}' has no textures table. Skipping.", atomName);
				continue;
			}

			boolean isBase64 = AtomCompiler.getOrDefault(atom, "textures.encoding", "path").equals("base64");

			// Iterate over all entries in the table
			for (String face : texturesTable.dottedKeySet()) {
				String tex = texturesTable.getString(face);

				if (tex == null) {
					LOGGER.warn("Texture for face '{}' in atom '{}' is null. Skipping.", face, atomName);
					continue;
				}

				// CREATING TEXTURE INTO ATOMS TEXTURES FOLDER
				String texPath;
				if (isBase64) {
					LOGGER.info("Texture '{}' for atom '{}' is base64.", face, atomName);
					String relativePath = AtomLoader.loadB64PNG(tex, atomName, face);
					texPath = relativePath;
				} else {
					LOGGER.info("Texture '{}' for atom '{}' is a path.", face, atomName);
					texPath = tex;
				}

				// REGISTER THE TEXTURE
				NamespaceID id = NamespaceID.getPermanent(MOD_ID, texPath);
				LOGGER.info("Registering texture '{}' for atom '{}'.", id, atomName);

				((AtlasStitcherAccessor) TextureRegistry.blockAtlas).callGetTexture(id);
			}

		}

		// READING BLOCK FIELDS
		LOGGER.info("Assigning block IDs from config...");

	}

	private void loadAtoms(List<TomlParseResult> atoms) {
		try {

			// CREATE ATOMS MOD CONFIG WITH ALL LOADED BLOCKS NAMES
			List<String> keys = atoms.stream()
				.map(data -> AtomCompiler.getOrDefault(data, "meta.author", "Unnamed") + "_" + AtomCompiler.getOrDefault(data, "data.name", "UnnamedBlock"))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");

			for (TomlParseResult atom : atoms) {

				AtomCompiler.compile(atom);

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

		List<TomlParseResult> atoms = AtomDataCache.ATOMS;
		Language language = ((I18nAccessor) I18n.getInstance()).getLanguage();
		LanguageAccessor lang = (LanguageAccessor) language;

		atoms.forEach(atom -> {
			String langkey = AtomCompiler.getOrDefault(atom, "lang.key", null);
			if (langkey == null) {
				LOGGER.warn("Atom missing langkey. Skipping language registration.");
				return;
			}

			String langid = language.getId();

			TomlTable langTable = atom.getTable("lang");
			if (langTable == null) {
				LOGGER.warn("Atom '{}' has no 'lang' section. Skipping.", langkey);
				return;
			}

			TomlTable localeTable = langTable.getTable(langid);
			if (localeTable == null) {
				LOGGER.warn("Atom '{}' has no entries for locale '{}'.", langkey, langid);
				return;
			}

			String displayName = localeTable.getString("name");
			String description = localeTable.getString("desc");
			String tooltip = localeTable.getString("tooltip");

			if (displayName != null)
				lang.getEntries().put("tile.atoms." + langkey + ".name", displayName);
			if (description != null)
				lang.getEntries().put("tile.atoms." + langkey + ".desc", description);
			if (tooltip != null)
				lang.getEntries().put("tile.atoms." + langkey + ".tooltip", tooltip);

			LOGGER.info(
				"Loaded language entries for '{}': name='{}', desc='{}', tooltip='{}'",
				langkey, displayName, description, tooltip
			);
		});

	}


}
