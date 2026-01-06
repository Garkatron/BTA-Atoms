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

	private static final BlockBuilder GENERIC_BLOCK_BUILDER = new BlockBuilder(MOD_ID)
		.setBlockSound(BlockSounds.STONE)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE);

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization started.");


		try {
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


		LOGGER.info("Registering textures for {} atoms...", atoms.size());

		for (TomlParseResult atom : atoms) {
			String atomName = atom.getString("name");
			if (atomName == null) {
				LOGGER.warn("Encountered atom without a name. Skipping texture registration.");
				continue;
			}

			TomlArray texturesArray = atom.getArray("textures");
			if (texturesArray == null) {
				LOGGER.warn("Atom '{}' has no textures array. Skipping.", atomName);
				continue;
			}

			boolean isBase64 = Boolean.TRUE.equals(atom.getBoolean("base64"));

			for (int i = 0; i < texturesArray.size(); i++) {
				TomlArray pair = texturesArray.getArray(i);
				if (pair == null || pair.size() != 2) {
					LOGGER.warn("Invalid texture entry for atom '{}'. Skipping entry index {}.",
						atomName, i);
					continue;
				}

				String face = pair.getString(0);
				String tex = pair.getString(1);

				String texPath;
				if (isBase64) {
					LOGGER.info("Texture is base64.");
					String relativePath = AtomLoader.loadB64PNG(tex, atomName, face);
					texPath = relativePath;
				} else {
					LOGGER.info("Texture is a path.");
					texPath = tex;
				}

				NamespaceID id = NamespaceID.getPermanent(MOD_ID, texPath);
				LOGGER.info("Registering texture '{}' for atom '{}'.", id, atomName);

				((AtlasStitcherAccessor) TextureRegistry.blockAtlas).callGetTexture(id);
			}

			LoadingProgressBar.loadingProgress++;
		}

		LOGGER.info("Assigning block IDs from config...");

		try {
			List<String> keys = atoms.stream()
				.map(a -> a.getString("author") + "_" + a.getString("name"))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			LOGGER.info("Creating block instances for atoms...");

			for (TomlParseResult atom : atoms) {
				String author = atom.getString("author");
				String atomName = atom.getString("name");
				if (atomName == null) atomName = "<Unnamed>";
				String key = author + "_" + atomName;

				String langkey = atom.getString("langkey");

				boolean iscubeshaped = Boolean.TRUE.equals(atom.getBoolean("iscubeshaped"));
				boolean iscollidable = Boolean.TRUE.equals(atom.getBoolean("iscollidable"));
				boolean issolidrender = Boolean.TRUE.equals(atom.getBoolean("issolidrender"));
				String materialName = atom.getString("material");
				Material material = MaterialUtils.MATERIALS.getOrDefault(materialName, Material.wood);

				LOGGER.debug("Creating block '{}' with key '{}'.", atomName, key);

				blocks.add(GENERIC_BLOCK_BUILDER.build(
					langkey,
					atomName,
					blockGoc(key),
					b -> new AtomBlockLogic(b, material, iscubeshaped, iscollidable, issolidrender)
				));
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
			String langkey = atom.getString("langkey");
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

			if (displayName != null)
				lang.getEntries().put("tile.atoms." + langkey + ".name", displayName);
			if (description != null)
				lang.getEntries().put("tile.atoms." + langkey + ".desc", description);

			LOGGER.info("Loaded language entries for '{}': name='{}', desc='{}'",
				langkey, displayName, description);
		});
	}


}
