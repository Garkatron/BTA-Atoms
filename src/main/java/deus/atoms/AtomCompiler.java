package deus.atoms;

import deus.atoms.blocks.AtomBlockLogic;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.BlockBuilder;

import java.util.ArrayList;
import java.util.List;

import static deus.atoms.ConfigManager.blockGoc;
import static deus.atoms.Main.blocks;

public class AtomCompiler {

	public static final int AtomFormatVersion = 1;

	@SuppressWarnings("unchecked")
	public static <T> T getOrDefault(TomlTable data, String key, T fallback) {
		if (data.contains(key)) {
			Object value = data.get(key);
			if (value != null) {
				// Double → Float
				if (fallback instanceof Float && value instanceof Double) {
					return (T) Float.valueOf(((Double) value).floatValue());
				}
				// Double → Integer
				else if (fallback instanceof Integer && value instanceof Double) {
					return (T) Integer.valueOf(((Double) value).intValue());
				}
				// Long → Integer
				else if (fallback instanceof Integer && value instanceof Long) {
					return (T) Integer.valueOf(((Long) value).intValue());
				}
				return (T) value;
			}
		}
		return fallback;
	}


	public static void compile(TomlParseResult data) {

// META
		String author = getOrDefault(data, "meta.author", "unknown");
		int version = AtomCompiler.getOrDefault(data, "meta.format_version", 0);


// LANG
		String langkey = getOrDefault(data, "lang.key", "en_US");

// DATA
		String atom_name = getOrDefault(data, "data.name", "Unnamed");

		if (version != AtomCompiler.AtomFormatVersion) {
			Main.LOGGER.warn("Wrong format version for block '{}'.", atom_name);
			return;
		}

		TomlArray tags = data.getArray("data.tags");
		boolean immovable = getOrDefault(data, "data.immovable", false);
		boolean unbreakable = getOrDefault(data, "data.unbreakable", false);

		String material = getOrDefault(data, "data.material", "STONE");
		float resistance = getOrDefault(data, "data.resistance", 0.0f);
		float slipperiness = getOrDefault(data, "data.slipperiness", 0.6f);
		float hardness = getOrDefault(data, "data.hardness", 1.0f);
		float luminance = getOrDefault(data, "data.luminance", 1.0f);

// SOUND
		String sound = getOrDefault(data, "sounds.sound", "STONE");

// FLAMMABILITY
		int chance_to_catch_fire = (int) getOrDefault(data, "flammability.chance_to_catch_fire", 0);
		int change_to_degrade = (int) getOrDefault(data, "flammability.change_to_degrade", 0);

// RENDER DATA
		boolean is_cube_shaped = getOrDefault(data, "render.is_cube_shaped", false);
		boolean is_solid_render = getOrDefault(data, "render.is_solid_render", false);

// PHYSICS DATA
		boolean is_collidable = getOrDefault(data, "physics.is_collidable", false);

// TEXTURES DATA
		// String encoding = getOrDefault(data, "textures.encoding", "base64");

// DROPS DATA
		TomlArray drops_array = data.getArray("events.break.drops");
		/*
			TomlTable drop = dropsArray.getTable(i);
			String item = drop.getString("item");
			String cause = drop.getString("cause");
			long chance = drop.getLong("chance");
		 */

		Material materialObject = EnumUtils.MATERIALS.getOrDefault(material, Material.wood);
		BlockSound blockSoundObject = EnumUtils.BLOCK_SOUNDS.getOrDefault(sound, BlockSounds.STONE);


		// ! BUILDING
		String key = author + "_" + atom_name;


		// BLOCK BUILDER
		// TODO: Figure out why this thing doesn't work at all.
		BlockBuilder BLOCK_BUILDER = new BlockBuilder(Main.MOD_ID)
			.setBlockSound(blockSoundObject)
			.setFlammability(chance_to_catch_fire, change_to_degrade)
			.setHardness(hardness)
			// .setLuminance(luminance) !
			.setSlipperiness(slipperiness)
			.setResistance(resistance);



		if (tags != null) {
			for (int i = 0; i < tags.size(); i++) {
				String tagString = tags.getString(i);
				BLOCK_BUILDER.addTags(EnumUtils.BLOCK_TAGS.get(tagString));
			}
		}

		Main.LOGGER.debug("Creating block '{}' with key '{}'.", atom_name, key);

		// BUILDING BLOCKS
		blocks.add(BLOCK_BUILDER.build(
			langkey,
			atom_name,
			blockGoc(key),
			b -> {
				b.withLightEmission(luminance);

				if (unbreakable) b.withSetUnbreakable();
				if (immovable) b.withImmovableFlagSet();

				return new AtomBlockLogic(
					b,
					materialObject,
					is_cube_shaped,
					is_collidable,
					is_solid_render,
					drops_array
				);
			}
		));

	}


}

