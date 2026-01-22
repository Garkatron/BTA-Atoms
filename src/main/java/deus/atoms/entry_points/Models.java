package deus.atoms.entry_points;

import deus.atoms.enums.BlockTypes;
import deus.atoms.toml.AtomDataCache;
import deus.atoms.Main;
import deus.atoms.toml.project.ProjectProcessed;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static deus.atoms.Main.LOGGER;
import static deus.atoms.toml.AtomLoader.TEXTURE_PATHS;
import static deus.atoms.Main.MOD_ID;

public class Models implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher blockModelDispatcher) {
		LOGGER.info("Initializing block models.");

		// Recolectar todos los bloques compilados de todos los proyectos
		List<CompiledBlock> allBlocks = new ArrayList<>();
		for (ProjectProcessed project : Main.PROJECTS) {
			List<CompiledBlock> projectBlocks = (List<CompiledBlock>) project.resources.getAtoms().get(AtomType.BLOCK);
			if (projectBlocks != null) {
				allBlocks.addAll(projectBlocks);
			}
		}

		// Procesar cada bloque registrado
		for (ProjectProcessed project : Main.PROJECTS) {
			project.blocks.forEach((key, block) -> {
				Optional<CompiledBlock> atomOpt = findAtomForBlock(block, allBlocks);

				if (!atomOpt.isPresent()) return;

				CompiledBlock atom = atomOpt.get();

				if (atom.textures == null || atom.textures.faces == null) return;

				// Crear el modelo apropiado
				BlockModelStandard<?> model = createBlockModel(block, atom);

				// Aplicar texturas
				applyTextures(model, atom);

				// Registrar el modelo
				BlockModelStandard<?> finalModel = model;
				ModelHelper.setBlockModel(block, () -> finalModel);
			});
		}
	}

	private Optional<CompiledBlock> findAtomForBlock(Block<?> block, List<CompiledBlock> atoms) {
		String blockId = block.namespaceId().toString();

		return atoms.stream()
			.filter(atom -> atom.data.name != null && blockId.contains(atom.data.name))
			.findFirst();
	}

	private BlockModelStandard<?> createBlockModel(Block<?> block, CompiledBlock atom) {
		// Determinar el tipo de modelo
		BlockTypes blockType = BlockTypes.fromString(
			atom.model != null ? atom.model.type : null
		);

		try {
			return blockType.createModel(block);
		} catch (Exception e) {
			LOGGER.error("Failed creating model for block '{}', using default",
				atom.data.name, e);
			return new BlockModelStandard<>(block);
		}
	}

	private void applyTextures(BlockModelStandard<?> model, CompiledBlock atom) {
		boolean isBase64 = "base64".equals(atom.textures.encoding);
		CompiledBlock.Textures.Faces faces = atom.textures.faces;

		// Aplicar cada cara
		applyFaceTexture(model, "top", faces.top, isBase64, atom.data.name);
		applyFaceTexture(model, "bottom", faces.bottom, isBase64, atom.data.name);
		applyFaceTexture(model, "north", faces.north, isBase64, atom.data.name);
		applyFaceTexture(model, "south", faces.south, isBase64, atom.data.name);
		applyFaceTexture(model, "east", faces.east, isBase64, atom.data.name);
		applyFaceTexture(model, "west", faces.west, isBase64, atom.data.name);
	}

	private void applyFaceTexture(
		BlockModelStandard<?> model,
		String faceName,
		String texture,
		boolean isBase64,
		String blockName
	) {
		if (texture == null) return;

		// Procesar textura base64 si es necesario

		String texturePath = texture;
		String cacheKey = blockName + "_" + faceName;
		/*String cachedPath = TEXTURE_PATHS.get(cacheKey);
		if (cachedPath != null) {
			texturePath = MOD_ID + ":block/" + cachedPath;
		}*/

		// Aplicar la textura a la cara correspondiente
		Side side = getSideFromFaceName(faceName);
		if (side != null) {
			model.setTex(0, texturePath, side);
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

		// Recolectar todos los items compilados de todos los proyectos
		List<CompiledItem> allItems = new ArrayList<>();
		for (ProjectProcessed project : Main.PROJECTS) {
			List<CompiledItem> projectItems = (List<CompiledItem>) project.resources.getAtoms().get(AtomType.ITEM);
			if (projectItems != null) {
				allItems.addAll(projectItems);
			}
		}

		// Procesar cada item registrado
		for (ProjectProcessed project : Main.PROJECTS) {
			project.items.forEach((key, item) -> {
				Optional<CompiledItem> atomOpt = allItems.stream()
					.filter(a -> {
						String name = a.data.name;
						return name != null && item.namespaceID.toString().contains(name);
					})
					.findFirst();

				if (!atomOpt.isPresent()) return;

				CompiledItem atom = atomOpt.get();
				CompiledItem.Textures texture = atom.textures;

				if (texture == null) return;

				ModelHelper.setItemModel(item,
					() -> {
						ItemModelStandard model = new ItemModelStandard(item, MOD_ID);
						model.icon = TextureRegistry.getTexture(item.namespaceID);
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
