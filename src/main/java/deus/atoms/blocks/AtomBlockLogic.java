package deus.atoms.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;

public class AtomBlockLogic extends BlockLogic {

	private final boolean cubeShaped;
	private final boolean collidable;
	private final boolean solidRender;

	public AtomBlockLogic(Block<?> block, Material material,
						  boolean cubeShaped,
						  boolean collidable,
						  boolean solidRender) {
		super(block, material);
		this.cubeShaped = cubeShaped;
		this.collidable = collidable;
		this.solidRender = solidRender;
	}

	@Override
	public boolean isCubeShaped() {
		return cubeShaped;
	}

	@Override
	public boolean isCollidable() {
		return collidable;
	}

	@Override
	public boolean isSolidRender() {
		return solidRender;
	}

}
