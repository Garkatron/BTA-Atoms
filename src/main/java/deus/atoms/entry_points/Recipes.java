package deus.atoms.entry_points;

import deus.atoms.AtomCompiler;
import deus.atoms.AtomDataCache;
import deus.atoms.Main;
import deus.atoms.utils.CompiledBlock;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderShaped;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static deus.atoms.ConfigManager.blockGoc;
import static deus.atoms.Main.MOD_ID;


public class Recipes implements RecipeEntrypoint {

	public static final RecipeNamespace ATOMS_RECIPE_NAMESPACE = new RecipeNamespace();

	@Override
	public void initNamespaces() {
		Registries.RECIPES.register(MOD_ID, ATOMS_RECIPE_NAMESPACE);
	}

	@Override
	public void onRecipesReady() {

		List<CompiledBlock> atoms = AtomDataCache.ATOMS;

		for (CompiledBlock atom : atoms) {



			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) {
				Main.LOGGER.warn("Wrong format version for block '{}'.", atom.data.name);
				continue;
			}

			// RECIPE
			boolean enable_workbench = atom.recipe.enableWorkbench;
			String recipeKey = atom.meta.author + "_" + atom.data.name;

			if (enable_workbench || atom.recipe.workbench.outputAmount > 0) {
				List<List<String>> pattern = atom.recipe.workbench.pattern;
				if (pattern.isEmpty()) continue;

				String[] shape = pattern.stream()
					.map(row -> String.join("", row))
					.toArray(String[]::new);

				RecipeBuilderShaped recipeBuilderShaped = RecipeBuilder.Shaped(MOD_ID)
					.setShape(shape);

				atom.recipe.workbench.symbols.forEach(symbolMap -> {
					symbolMap.forEach((k, v) -> {
						recipeBuilderShaped.addInput(
							k.charAt(0),
							Item.getItem(v)
						);
					});
				});
				recipeBuilderShaped.create(MOD_ID + ":" + recipeKey, new ItemStack(Blocks.getBlock(blockGoc(recipeKey)).asItem(), atom.recipe.workbench.outputAmount));
			}

			boolean enable_furnace = atom.recipe.enableWorkbench;

			if (!enable_furnace || atom.recipe.furnace.out_item_id > 0) {
				RecipeBuilderFurnace recipeBuilderFurnace = new RecipeBuilderFurnace(MOD_ID)
					.setInput(Blocks.getBlock(blockGoc(recipeKey)));

				recipeBuilderFurnace.create(MOD_ID + ":" + recipeKey, new ItemStack(atom.recipe.furnace.out_item_id, atom.recipe.furnace.output_amount, 0));
			}


		}

	}


}
