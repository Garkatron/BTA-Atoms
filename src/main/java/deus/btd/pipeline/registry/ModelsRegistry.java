package deus.btd.pipeline.registry;

import deus.btd.Main;
import deus.btd.blocks.DynBlockModel;
import deus.btd.enums.BlockTypes;
import deus.btd.pipeline.project.ProjectProcessed;
import deus.btd.pipeline.compile.types.AtomType;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static deus.btd.Main.*;
import static deus.btd.pipeline.registry.AtomProjectRegistry.PROJECTS;

public class ModelsRegistry implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher blockModelDispatcher) {
		LOGGER.info("Initializing block models.");

		ModelHelper.setBlockModel(DYN_BLOCK, () -> new DynBlockModel(DYN_BLOCK));




		for (ProjectProcessed project : PROJECTS) {
			for (CompiledBlock atom : (List<CompiledBlock>) project.atoms.get(AtomType.BLOCK)) {



				if (atom.textures == null || atom.textures.faces == null) return;

				BlockModelStandard<?> model = createBlockModel(DYN_BLOCK, atom);

				applyTextures(model, atom);

				atom.blockModel = model;
				// ModelHelper.setBlockModel(block, () -> finalModel);
			}
		}
	}

	private Optional<CompiledBlock> findAtomForBlock(Block<?> block, List<CompiledBlock> atoms) {
		String blockId = block.namespaceId().toString();

		return atoms.stream()
			.filter(atom -> atom.data.name != null && blockId.contains(atom.data.name))
			.findFirst();
	}

	private BlockModelStandard<?> createBlockModel(Block<?> block, CompiledBlock atom) {
		BlockTypes blockType = BlockTypes.fromString(
			atom.model != null ? atom.model.type : null
		);

		// TODO: FIX THIS SHIT
		BlockTypes.BlockContext ctx = BlockTypes.BlockContext.of(block);

		if (atom.model != null && atom.model.rootKey != null && !atom.model.rootKey.trim().isEmpty()) {
			ctx.withRootKey(atom.model.rootKey);
		}

		try {
			return blockType.createModel(ctx);
		} catch (Exception e) {
			LOGGER.error("Failed creating model for block '{}', using default",
				atom.data.name, e);
			return new BlockModelStandard<>(block);
		}
	}

	private void applyTextures(BlockModelStandard<?> model, CompiledBlock atom) {
		CompiledBlock.Textures.Faces faces = atom.textures.faces;

		applyFaceTexture(model, "top", faces.top);
		applyFaceTexture(model, "bottom", faces.bottom);
		applyFaceTexture(model, "north", faces.north);
		applyFaceTexture(model, "south", faces.south);
		applyFaceTexture(model, "east", faces.east);
		applyFaceTexture(model, "west", faces.west);
	}

	private void applyFaceTexture(
		BlockModelStandard<?> model,
		String faceName,
		String texture
	) {
		if (texture == null) return;
		Side side = getSideFromFaceName(faceName);
		if (side != null) {
			model.setTex(0, texture, side);
		}
	}

	private Side getSideFromFaceName(String faceName) {
		switch (faceName.toLowerCase()) {
			case "top":    return Side.TOP;
			case "bottom": return Side.BOTTOM;
			case "north":  return Side.NORTH;
			case "south":  return Side.SOUTH;
			case "east":   return Side.EAST;
			case "west":   return Side.WEST;
			default:       return null;
		}
	}

	@Override
	public void initItemModels(ItemModelDispatcher itemModelDispatcher) {
		LOGGER.info("Initializing item models.");

		List<CompiledItem> allItems = new ArrayList<>();
		for (ProjectProcessed project : PROJECTS) {
			List<CompiledItem> projectItems = (List<CompiledItem>) project.atoms.get(AtomType.ITEM);
			if (projectItems != null) {
				allItems.addAll(projectItems);
			}
		}

		for (ProjectProcessed project : PROJECTS) {
			project.items().forEach((key, item) -> {
				Optional<CompiledItem> atomOpt = allItems.stream()
					.filter(a -> {
						String name = a.data.name;
						return name != null && item.namespaceID.toString().contains(name);
					})
					.findFirst();

				if (!atomOpt.isPresent()) return;

				CompiledItem atom = atomOpt.get();
				CompiledItem.Textures texture = atom.textures;

				if (texture.texture == null) return;

				ModelHelper.setItemModel(item,
					() -> {
						ItemModelStandard model = new ItemModelStandard(item, MOD_ID);

						model.icon = TextureRegistry.getTexture(texture.texture);
						return model;
					});
			});
		}
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher entityRenderDispatcher) {
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher tileEntityRenderDispatcher) {
	}

	@Override
	public void initBlockColors(BlockColorDispatcher blockColorDispatcher) {
	}
}
