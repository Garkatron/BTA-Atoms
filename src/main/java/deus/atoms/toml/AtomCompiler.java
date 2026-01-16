package deus.atoms.toml;

import deus.atoms.enums.BlockTypes;
import deus.atoms.items.AtomItem;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import deus.atoms.utils.EnumUtils;
import deus.atoms.Main;
import deus.atoms.blocks.AtomBlockLogic;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.*;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import turniplabs.halplibe.helper.BlockBuilder;

import java.util.List;

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
		Material materialObject = EnumUtils.MATERIALS.getOrDefault(
			atom.data.material.toUpperCase(),
			Material.wood
		);

		BlockSound blockSoundObject = EnumUtils.BLOCK_SOUNDS.getOrDefault(
			atom.sounds.sound,
			BlockSounds.STONE
		);

		BlockTypes blockType = BlockTypes.fromString(atom.logic != null ? atom.logic.type : null);

		String key = (atom.meta.author + "_" + atom.data.name).trim();

		Main.LOGGER.debug("Creating block '{}' with type '{}' and key '{}'.",
			atom.data.name, blockType.name(), key);

		BlockBuilder builder = createBlockBuilder(atom, blockSoundObject);

		addBlockTags(builder, atom.data.tags);

		blocks.add(builder.build(
			atom.lang.key,
			atom.data.name,
			blockGoc(key),
			block -> createBlockLogic(block, blockType, materialObject, atom)
		));
	}

	private static BlockBuilder createBlockBuilder(CompiledBlock atom, BlockSound blockSound) {
		BlockBuilder builder = new BlockBuilder(Main.MOD_ID)
			.setBlockSound(blockSound)
			.setFlammability(atom.flammability.chanceToCatchFire, atom.flammability.changeToDegrade)
			.setHardness((float) atom.data.hardness)
			.setSlipperiness((float) atom.data.slipperiness)
			.setResistance((float) atom.data.resistance);

		return builder;
	}

	private static void addBlockTags(BlockBuilder builder, List<String> tags) {
		if (tags != null) {
			for (String tag : tags) {
				Tag<Block<?>> blockTag = EnumUtils.BLOCK_TAGS.get(tag);
				if (blockTag != null) {
					builder.addTags(blockTag);
				}
			}
		}
	}

	private static BlockLogic createBlockLogic(
		Block<?> block,
		BlockTypes blockType,
		Material material,
		CompiledBlock atom
	) {
		block.withLightEmission((float) atom.data.luminance);

		if (atom.data.unbreakable) {
			block.withSetUnbreakable();
		}

		if (atom.data.immovable) {
			block.withImmovableFlagSet();
		}

		if (blockType == BlockTypes.DEFAULT) {
			return new AtomBlockLogic(
				block,
				material,
				atom.render.isCubeShaped,
				atom.physics.isCollidable,
				atom.render.isSolidRender,
				atom.events.onBreak.dropItself,
				atom.events.onBreak.drops
			);
		}

		Block<?> baseModel = Blocks.getBlock(1);
		if (atom.model != null && atom.model.baseBlockId >= 1) {
			baseModel = Blocks.getBlock(atom.model.baseBlockId);
		}

		// Para otros tipos, usar la factory
		return blockType.createLogic(block, baseModel, material);
	}




}

