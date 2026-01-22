package deus.btd.toml;

import deus.btd.enums.BlockTypes;
import deus.btd.items.AtomItem;
import deus.btd.toml.types.CompiledBlock;
import deus.btd.toml.types.CompiledItem;
import deus.btd.utils.EnumUtils;
import deus.btd.Main;
import deus.btd.blocks.AtomBlockLogic;
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

import static deus.btd.Main.*;
import static deus.btd.toml.AtomLoader.formatAtomKey;
import static deus.btd.utils.ConfigManager.blockGoc;
import static deus.btd.utils.ConfigManager.itemGoc;

public class AtomCompiler {

	public static final int AtomFormatVersion = 1;

	public static Item convertIntoItem(CompiledItem atom) {
		ToolMaterial materialObject = EnumUtils.TOOL_MATERIALS.getOrDefault(atom.data.material.toUpperCase(), ToolMaterial.wood);

		// ! BUILDING
		String key = formatAtomKey(atom.meta.author, atom.data.name);

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


		return item;

	}



	public static Block<?> convertIntoBlocks(CompiledBlock atom) {
		Material materialObject = EnumUtils.MATERIALS.getOrDefault(
			atom.data.material.toUpperCase(),
			Material.wood
		);

		BlockSound blockSoundObject = EnumUtils.BLOCK_SOUNDS.getOrDefault(
			atom.sounds.sound,
			BlockSounds.STONE
		);

		BlockTypes blockType = BlockTypes.fromString(atom.logic != null ? atom.logic.type : null);

		String key = formatAtomKey(atom.meta.author, atom.data.name);

		Main.LOGGER.debug("Creating block '{}' with type '{}' and key '{}'.",
			atom.data.name, blockType.name(), key);

		BlockBuilder builder = createBlockBuilder(atom, blockSoundObject);

		addBlockTags(builder, atom.data.tags);

		return builder.build(
			atom.lang.key,
			atom.data.name,
			blockGoc(key),
			block -> createBlockLogic(block, blockType, materialObject, atom)
		);
	}

	private static BlockBuilder createBlockBuilder(CompiledBlock atom, BlockSound blockSound) {
		BlockBuilder builder = new BlockBuilder(atom.Namespace())
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

		return blockType.createLogic(block, baseModel, material);
	}





}

