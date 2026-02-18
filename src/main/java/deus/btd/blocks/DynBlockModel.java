package deus.btd.blocks;

import deus.btd.pipeline.compile.types.CompiledBlock;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
public class DynBlockModel extends BlockModelStandard<DynBlockLogic> {

	public DynBlockModel(Block<DynBlockLogic> block) {
		super(block);
	}

	private BlockModel<?> resolve(WorldSource w, int x, int y, int z){
		TileEntity te = ((World) w).getTileEntity(x, y, z);
		if (te instanceof DynTileEntity) {
			CompiledBlock cb = ((DynTileEntity)te).getCompiled();
			if (cb != null) return cb.blockModel;
		}
		return null;
	}

	@Override
	public boolean render(Tessellator tess, int x, int y, int z) {
		BlockModel<?> m = resolve(renderBlocks.blockAccess, x, y, z);
		if (m != null) {
			return m.render(tess, x, y, z);
		}
		return false;
	}

	@Override
	public IconCoordinate getBlockTexture(WorldSource w, int x, int y, int z, Side side) {
		BlockModel<?> m = resolve(w, x, y, z);
		if (m instanceof BlockModelStandard<?>)
			return m.getBlockTexture(w, x, y, z, side);
		return BLOCK_TEXTURE_MISSING;
	}
}
