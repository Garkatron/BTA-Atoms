package deus.atoms.entry_points;

import deus.atoms.Main;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelHorizontalRotation;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static deus.atoms.Main.MOD_ID;

public class Models implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher blockModelDispatcher) {
		for (Block<?> block : Main.blocks) {
			ModelHelper.setBlockModel(block, () -> new BlockModelHorizontalRotation<>(block)
				.setTex(0, block.namespaceId().toString(), Side.sides)
			);
		}

	}

	@Override
	public void initItemModels(ItemModelDispatcher itemModelDispatcher) {

	}
e
	@Override
	public void initEntityModels(EntityRenderDispatcher entityRenderDispatcher) {
		// ! EntityModelInitializer.initialize("deus.paperwork", entityRenderDispatcher);

	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher tileEntityRenderDispatcher) {

	}

	@Override
	public void initBlockColors(BlockColorDispatcher blockColorDispatcher) {

	}
}
