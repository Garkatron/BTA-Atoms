package deus.atoms;

import deus.atoms.blocks.AtomBlockLogic;
import deus.atoms.utils.CompiledBlock;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.BlockBuilder;

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


	public static void convertoIntoBlocks(CompiledBlock atom) {


		Material materialObject = EnumUtils.MATERIALS.getOrDefault(atom.data.material, Material.wood);
		BlockSound blockSoundObject = EnumUtils.BLOCK_SOUNDS.getOrDefault(atom.sounds.sound, BlockSounds.STONE);


		// ! BUILDING
		String key = atom.meta.author + "_" + atom.data.name;


		// BLOCK BUILDER
		// TODO: Figure out why this thing doesn't work at all.
		BlockBuilder BLOCK_BUILDER = new BlockBuilder(Main.MOD_ID)
			.setBlockSound(blockSoundObject)
			.setFlammability(atom.flammability.chanceToCatchFire, atom.flammability.changeToDegrade)
			.setHardness((float) atom.data.hardness)
			// .setLuminance(luminance) !
			.setSlipperiness((float) atom.data.slipperiness)
			.setResistance((float) atom.data.resistance);



		if (atom.data.tags != null) {
			for (String tag : atom.data.tags) {
				BLOCK_BUILDER.addTags(EnumUtils.BLOCK_TAGS.get(tag));
			}
		}

		Main.LOGGER.debug("Creating block '{}' with key '{}'.", atom.data.name, key);

		// BUILDING BLOCKS
		blocks.add(BLOCK_BUILDER.build(
			atom.lang.key,
			atom.data.name,
			blockGoc(key),
			b -> {
				b.withLightEmission((float) atom.data.luminance);

				if (atom.data.unbreakable) b.withSetUnbreakable();
				if (atom.data.immovable) b.withImmovableFlagSet();

				return new AtomBlockLogic(
					b,
					materialObject,
					atom.render.isCubeShaped,
					atom.physics.isCollidable,
					atom.render.isSolidRender,
					atom.events.onBreak.dropItself,
					atom.events.onBreak.drops
				);
			}
		));

	}


}

