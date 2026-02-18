package deus.btd.items;

import com.mojang.nbt.tags.CompoundTag;
import deus.btd.blocks.DynBlockLogic;
import deus.btd.blocks.DynTileEntity;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.data.gamerule.GameRuleCollection.writeToNBT;

public class DynItemBlock extends ItemBlock<DynBlockLogic> {
	public DynItemBlock(@NotNull Block<DynBlockLogic> block) {
		super(block);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack stack, @Nullable Player player, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {

		boolean result = super.onUseItemOnBlock(
			stack, player, world, x, y, z, side, xPlaced, yPlaced
		);

		if (!result) return false;

		TileEntity te = world.getTileEntity(x, y, z);
		String dynblocknamespace = stack.getData().getString("dynblocknamespace");


		if (te instanceof DynTileEntity) {
			try {
				((DynTileEntity)te).setNamespace(NamespaceID.getPermanent(dynblocknamespace));
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}

		return true;
	}
}
