package deus.atoms.toml;

import deus.atoms.items.AtomItem;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import deus.atoms.utils.EnumUtils;
import deus.atoms.Main;
import deus.atoms.blocks.AtomBlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.*;
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
		ToolMaterial materialObject = EnumUtils.TOOL_MATERIALS.getOrDefault(atom.data.material.toUpperCase(), ToolMaterial.wood);

		// ! BUILDING
		String key = (atom.meta.author + "_" + atom.data.name).trim();

		if (atom.data.tags != null) {
			for (String tag : atom.data.tags) {
				// BLOCK_BUILDER.addTags(EnumUtils.BLOCK_TAGS.get(tag));
			}
		}

		Main.LOGGER.debug("Creating block '{}' with key '{}'.", atom.data.name, key);

		Item item;

		if (atom.tool != null) {
			switch (atom.tool.type.toUpperCase()) {
				case "AXE":
					item = new ItemToolAxe(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "SWORD":
					item = new ItemToolSword(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "PICKAXE":
					item = new ItemToolPickaxe(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "SHOVEL":
					item = new ItemToolShovel(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "SHEARS":
					item = new ItemToolShears(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "HOE":
					item = new ItemToolHoe(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), materialObject);
					break;
				case "DEFAULT":
				default:
					item = new AtomItem(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key));
					break;
			}
		} else if (atom.food != null) {
			item = new ItemFood(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key), atom.food.healAmount, atom.food.ticksPerHeal, atom.food.favouriteWolfMeat, atom.data.maxStackSize);
		} else {
			item = new AtomItem(atom.lang.key, MOD_ID + ":item/" + atom.data.name + "/" + atom.data.name, itemGoc(key));
		}


		items.add(item);

	}



	public static void convertoIntoBlocks(CompiledBlock atom) {
		Material materialObject = EnumUtils.MATERIALS.getOrDefault(atom.data.material.toUpperCase(), Material.wood);
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

