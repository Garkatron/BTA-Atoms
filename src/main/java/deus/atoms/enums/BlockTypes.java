package deus.atoms.enums;


import deus.atoms.blocks.AtomBlockLogic;
import net.minecraft.client.render.block.model.*;
import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;

import java.util.ArrayList;
import java.util.List;


public enum BlockTypes {

	DEFAULT(
		(block, model, mat) -> new AtomBlockLogic(block, mat, true, true, true, true, new ArrayList<>()),
		BlockModelStandard::new
	),

	FURNACE(
		(block, model, mat) -> new BlockLogicFurnace(block, false),
		BlockModelStandard::new
	),

	BLAST_FURNACE(
		(block, model, mat) -> new BlockLogicFurnaceBlast(block, false),
		BlockModelStandard::new
	),

	WORKBENCH(
		(block, model, mat) -> new BlockLogicWorkbench(block),
		BlockModelStandard::new
	),

	BUTTON(
		(block, model, mat) -> new BlockLogicButton(block),
		BlockModelStandard::new
	),

	STAIRS(
		(block, model, mat) -> new BlockLogicStairs(block, model),
		(d) -> new BlockModelStairs(d)
	),

	CHEST(
		(block, model, mat) -> new BlockLogicChest(block, (Material) mat),
		b -> new BlockModelChest(b, "test")
	);

	@FunctionalInterface
	public interface LogicFactory {
		BlockLogic create(Block<?> block, Block<?> modelBlock, Material material);
	}

	@FunctionalInterface
	public interface ModelFactory {
		BlockModelStandard create(Block<?> block);
	}

	private final LogicFactory logicFactory;
	private final ModelFactory modelFactory;

	BlockTypes(LogicFactory logicFactory, ModelFactory modelFactory) {
		this.logicFactory = logicFactory;
		this.modelFactory = modelFactory;
	}

	public BlockLogic createLogic(Block<?> block, Block<?> modelBlock, Material material) {
		return logicFactory.create(block, modelBlock, material);
	}

	public BlockModelStandard createModel(Block<?> block) {
		return modelFactory.create(block);
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
