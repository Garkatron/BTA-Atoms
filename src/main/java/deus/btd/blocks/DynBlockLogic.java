package deus.btd.blocks;

import deus.btd.pipeline.compile.types.CompiledBlock;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Objects;
import java.util.function.Supplier;

public class DynBlockLogic extends BlockLogic {
	public DynBlockLogic(Block<?> block) {
		super(block, Material.air);
	}
	private CompiledBlock c(World w, int x, int y, int z){
		TileEntity te = w.getTileEntity(x,y,z);
		if(te instanceof DynTileEntity){
			return ((DynTileEntity) te).getCompiled();
		}
		return null;
	}

	@Override
	public float blockStrength(World world, int x, int y, int z, Side side, Player player) {
		return (float) Objects.requireNonNull(c(world, x, y, z)).data.hardness;
	}
}
