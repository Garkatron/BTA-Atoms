package deus.atoms.entry_points;

import deus.atoms.AtomDataCache;
import deus.atoms.Main;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import java.util.List;
import java.util.Optional;

import static deus.atoms.AtomLoader.TEXTURE_PATHS;
import static deus.atoms.Main.MOD_ID;

public class Models implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher blockModelDispatcher) {

		List<TomlParseResult> atoms = AtomDataCache.ATOMS;

		for (Block<?> block : Main.blocks) {

			Optional<TomlParseResult> atomOpt = atoms.stream()
				.filter(a -> {
					String name = a.getString("name");
					return name != null && block.namespaceId().toString().contains(name);
				})
				.findFirst();

			if (!atomOpt.isPresent()) continue;

			TomlParseResult atom = atomOpt.get();
			TomlArray texturesArray = atom.getArray("textures");

			if (texturesArray == null || texturesArray.isEmpty()) continue;

			BlockModelStandard model = new BlockModelStandard<>(block);
			String atomName = atom.getString("name");
			boolean b64 = Boolean.TRUE.equals(atom.getBoolean("base64"));

			for (int i = 0; i < texturesArray.size(); i++) {
				TomlArray pair = texturesArray.getArray(i);
				if (pair == null || pair.size() != 2) continue;

				String face = pair.getString(0);
				String tex = pair.getString(1);
				// System.out.println("TEEEEEEEEEEEEEEEEEEEX: " + tex);

				// Si es base64, obtener la ruta del cache en lugar de regenerar
				if (b64) {
					String cacheKey = atomName + "_" + face;
					String cachedPath = TEXTURE_PATHS.get(cacheKey);
					if (cachedPath != null) {
						// System.out.println("CACHE: " + cachedPath);
						tex = MOD_ID + ":block/" + cachedPath;
					}
				}

				// tex = tex.split(":")[0]+":block/"+tex.split(":")[1];
				switch (face) {
					case "all":
					case "side":
						model.setTex(0, tex, Side.sides);
						break;
					case "top":
						model.setTex(0, tex, Side.TOP);
						break;
					case "bottom":
						model.setTex(0, tex, Side.BOTTOM);
						break;
					case "north":
						model.setTex(0, tex, Side.NORTH);
						break;
					case "south":
						model.setTex(0, tex, Side.SOUTH);
						break;
					case "east":
						model.setTex(0, tex, Side.EAST);
						break;
					case "west":
						model.setTex(0, tex, Side.WEST);
						break;
				}
			}

			ModelHelper.setBlockModel(block, () -> model);
		}
	}

	@Override
	public void initItemModels(ItemModelDispatcher itemModelDispatcher) {
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
