package deus.atoms;

import deus.atoms.entry_points.Sounds;
import deus.atoms.mixin.AtlasStitcherAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static deus.atoms.AtomLoader.ATOMS_TEXTURES_PATH;
import static deus.atoms.ConfigManager.blockGoc;
import static deus.atoms.ConfigManager.configBlockIDsFromNames;

public class Main implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "atoms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<Block<?>> blocks = new ArrayList<>();

	// Atlas dinámico para texturas externas

	private static final BlockBuilder GENERIC_BLOCK_BUILDER = new BlockBuilder(MOD_ID)
		.setBlockSound(BlockSounds.STONE)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE);




	@Override
	public void beforeGameStart() {
		try {
			// Cargar atoms y registrar texturas ANTES del atlas
			List<TomlParseResult> atoms = AtomLoader.loadAllAtoms();

			for (TomlParseResult atom : atoms) {
				String atomName = atom.getString("name");
				if (atomName == null) continue;

				// Registrar la textura en el atlas
				((AtlasStitcherAccessor)TextureRegistry.blockAtlas).callGetTexture(
					NamespaceID.getPermanent(MOD_ID, atomName)
				);
			}

		} catch (IOException e) {
			LOGGER.error("Failed to pre-register textures", e);
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("[{}] Core Initialized.", MOD_ID);

		try {
			List<TomlParseResult> atoms = AtomLoader.loadAllAtoms();
			List<String> keys = atoms.stream()
				.map(a -> a.getString("author") + "_" + a.getString("name"))
				.filter(k -> k != null)
				.collect(Collectors.toList());

			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			for (TomlParseResult atom : atoms) {
				String author = atom.getString("author");
				String atomName = atom.getString("name");
				if (atomName == null) atomName = "<Unnamed>";
				String key = author + "_" + atomName;

				blocks.add(GENERIC_BLOCK_BUILDER
					.build(
						atom.getString("langkey"),
						atomName,
						blockGoc(key),
						b -> new BlockLogic(b, Material.steel)
					));
			}

		} catch (IOException e) {
			LOGGER.error("[{}] Failed to load atoms", MOD_ID, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterGameStart() {

	}



}
