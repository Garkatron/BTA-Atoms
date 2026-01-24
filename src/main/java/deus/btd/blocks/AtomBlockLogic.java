package deus.btd.blocks;

import deus.btd.Main;
import deus.btd.pipeline.compile.types.CompiledBlock;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AtomBlockLogic extends BlockLogic {

	private final boolean cubeShaped;
	private final boolean collidable;
	private final boolean solidRender;
	private final boolean dropItself;
	private List<CompiledBlock.Events.BreakEvent.Drops>  drops = null;

	public AtomBlockLogic(Block<?> block, Material material,
						  boolean cubeShaped,
						  boolean collidable,
						  boolean solidRender, boolean dropItself, List<CompiledBlock.Events.BreakEvent.Drops> drops) {
		super(block, material);
		this.cubeShaped = cubeShaped;
		this.collidable = collidable;
		this.solidRender = solidRender;
		this.dropItself = dropItself;
		this.drops = drops;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {

		if (this.dropItself && this.drops == null) return super.getBreakResult(world, dropCause, meta, tileEntity);

		List<ItemStack> drops = new ArrayList<>();

		for (int i = 0; i < this.drops.size(); i++) {
			CompiledBlock.Events.BreakEvent.Drops drop = this.drops.get(i);
			if (drop == null) continue;

			int itemId = Math.toIntExact(drop.item);
			String causeStr = drop.cause;
			int chance = drop.chance;

			if (causeStr == null) continue;
			EnumDropCause cause;
			try {
				cause = EnumDropCause.valueOf(causeStr);
			} catch (IllegalArgumentException e) {
				Main.LOGGER.warn("Invalid drop cause '{}' in atom '{}'. Skipping.", causeStr, this.block.namespaceId());
				continue;
			}

			if (cause != dropCause) continue;
			if (chance <= 0) continue;

			if (chance < 100) {
				int roll = world.rand.nextInt(100) + 1;
				if (roll > chance) continue;
			}

			Item item = Item.getItem(itemId);

			if (item != null) {
				drops.add(new ItemStack(item));
			} else {
				Main.LOGGER.warn("Item '{}' not found for atom '{}'. Skipping.", itemId, this.block.namespaceId());
			}
		}

		return drops.isEmpty() ? super.getBreakResult(world, dropCause, meta, tileEntity) : drops.toArray(new ItemStack[0]);
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
