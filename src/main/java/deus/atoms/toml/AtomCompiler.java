package deus.atoms.toml;

import deus.atoms.items.AtomItem;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import deus.atoms.toml.types.CompiledItemTool;
import deus.atoms.utils.EnumUtils;
import deus.atoms.Main;
import deus.atoms.blocks.AtomBlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.BlockBuilder;

import static deus.atoms.Main.*;
import static deus.atoms.utils.ConfigManager.blockGoc;
import static deus.atoms.utils.ConfigManager.itemGoc;

public class AtomCompiler {

	public static final int AtomFormatVersion = 1;

	public static void convertIntoItem(CompiledItem atom) {
		// Material materialObject = EnumUtils.MATERIALS.getOrDefault(atom.data.material, Material.wood);


		// ! BUILDING
		String key = (atom.meta.author + "_" + atom.data.name).trim();

		if (atom.data.tags != null) {
			for (String tag : atom.data.tags) {
				// BLOCK_BUILDER.addTags(EnumUtils.BLOCK_TAGS.get(tag));
			}
		}

		Main.LOGGER.debug("Creating block '{}' with key '{}'.", atom.data.name, key);

		// BUILDING BLOCKS
		items.add(
			new AtomItem(atom.lang.key, MOD_ID + ":" +  atom.data.name, itemGoc(key))
		);

	}

	public static void convertIntoItemTool(CompiledItemTool atom) {

	}

	public static void convertoIntoBlocks(CompiledBlock atom) {
		Material materialObject = EnumUtils.MATERIALS.getOrDefault(atom.data.material, Material.wood);
		BlockSound blockSoundObject = EnumUtils.BLOCK_SOUNDS.getOrDefault(atom.sounds.sound, BlockSounds.STONE);


		// ! BUILDING
		String key = (atom.meta.author + "_" + atom.data.name).trim();


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

