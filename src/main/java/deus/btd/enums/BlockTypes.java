package deus.btd.enums;

import deus.btd.blocks.AtomBlockLogic;
import net.minecraft.client.render.block.model.*;
import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;

import java.util.ArrayList;

public enum BlockTypes {

	DEFAULT(
		ctx -> new AtomBlockLogic(ctx.block, ctx.material, true, true, true, true, new ArrayList<>()),
		ctx -> new BlockModelStandard(ctx.block)
	),

	FURNACE(
		ctx -> new BlockLogicFurnace(ctx.block, false),
		ctx -> new BlockModelFurnace(ctx.block)
	),

	BLAST_FURNACE(
		ctx -> new BlockLogicFurnaceBlast(ctx.block, false),
		ctx -> new BlockModelStandard(ctx.block)
	),

	WORKBENCH(
		ctx -> new BlockLogicWorkbench(ctx.block),
		ctx -> new BlockModelStandard(ctx.block)
	),

	BUTTON(
		ctx -> new BlockLogicButton(ctx.block),
		ctx -> new BlockModelStandard(ctx.block)
	),

	STAIRS(
		ctx -> new BlockLogicStairs(ctx.block, ctx.modelBlock),
		ctx -> new BlockModelStairs(ctx.block)
	),

	CHEST(
		ctx -> new BlockLogicChest(ctx.block, ctx.material),
		ctx -> new BlockModelChest(ctx.block, ctx.rootKey)
	),

	SLAB(
		ctx -> new BlockLogicSlab(ctx.block, ctx.modelBlock),
		ctx -> new BlockModelSlab(ctx.block)
	);

	@FunctionalInterface
	public interface Factory {
		BlockLogic createLogic(BlockContext ctx);
	}

	@FunctionalInterface
	public interface ModelFactory {
		BlockModelStandard createModel(BlockContext ctx);
	}

	public static class BlockContext {
		public final Block<?> block;
		public Block<?> modelBlock;
		public Material material;
		public String rootKey;

		private BlockContext(Block<?> block) {
			this.block = block;
		}

		public static BlockContext of(Block<?> block) {
			return new BlockContext(block);
		}

		public BlockContext withModelBlock(Block<?> modelBlock) {
			this.modelBlock = modelBlock;
			return this;
		}

		public BlockContext withMaterial(Material material) {
			this.material = material;
			return this;
		}

		public BlockContext withRootKey(String rootKey) {
			this.rootKey = rootKey;
			return this;
		}
	}

	private final Factory logicFactory;
	private final ModelFactory modelFactory;

	BlockTypes(Factory logicFactory, ModelFactory modelFactory) {
		this.logicFactory = logicFactory;
		this.modelFactory = modelFactory;
	}

	public BlockLogic createLogic(BlockContext ctx) {
		return logicFactory.createLogic(ctx);
	}

	public BlockModelStandard createModel(BlockContext ctx) {
		return modelFactory.createModel(ctx);
	}

	public static BlockTypes fromString(String name) {
		if (name == null || name.trim().isEmpty()) {
			return DEFAULT;
		}

		try {
			return valueOf(name.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return DEFAULT;
		}
	}
}
