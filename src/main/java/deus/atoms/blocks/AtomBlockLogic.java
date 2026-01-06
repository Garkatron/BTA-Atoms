package deus.atoms.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

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
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return super.getBreakResult(world, dropCause, meta, tileEntity);
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		super.onBlockRemoved(world, x, y, z, data);
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
