package deus.atoms.entry_points;

import deus.atoms.toml.AtomCompiler;
import deus.atoms.toml.AtomDataCache;
import deus.atoms.Main;
import deus.atoms.toml.project.ProjectProcessed;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderShaped;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.List;

import static deus.atoms.Main.LOGGER;
import static deus.atoms.utils.ConfigManager.blockGoc;
import static deus.atoms.utils.ConfigManager.itemGoc;
import static deus.atoms.Main.MOD_ID;


public class Recipes implements RecipeEntrypoint {

	public static final RecipeNamespace ATOMS_RECIPE_NAMESPACE = new RecipeNamespace();

	@Override
	public void initNamespaces() {
		LOGGER.info("Initializing namespaces.");
		Registries.RECIPES.register(MOD_ID, ATOMS_RECIPE_NAMESPACE);
	}

	@Override
	public void onRecipesReady() {
		LOGGER.info("Creating recipes.");
		for (ProjectProcessed project : Main.PROJECTS) {
			// Crear recetas para bloques
			List<CompiledBlock> blocks = (List<CompiledBlock>) project.resources.getAtoms().get(AtomType.BLOCK);
			if (blocks != null && !blocks.isEmpty()) {
				createBlockRecipes(blocks);
			}

			// Crear recetas para items
			List<CompiledItem> items = (List<CompiledItem>) project.resources.getAtoms().get(AtomType.ITEM);
			if (items != null && !items.isEmpty()) {
				createItemRecipes(items);
			}
		}
	}

	public void createBlockRecipes(List<CompiledBlock> atoms) {
		for (CompiledBlock atom : atoms) {
			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) {
				Main.LOGGER.warn("Wrong format version for block '{}'.", atom.data.name);
				continue;
			}

			if (atom.recipe == null ) continue;

			String recipeKey = atom.meta.author + "_" + atom.data.name;

			// Workbench recipe
			if (atom.recipe.workbench != null ) {
				createWorkbenchRecipe(
					atom.recipe.enableWorkbench,
					atom.recipe.workbench.pattern,
					atom.recipe.workbench.symbols,
					atom.recipe.workbench.outputAmount,
					recipeKey,
					new ItemStack(Blocks.getBlock(blockGoc(recipeKey)).asItem(), atom.recipe.workbench.outputAmount)
				);
			}

			// Furnace recipe
			if(atom.recipe.furnace != null) {
				createFurnaceRecipe(
					atom.recipe.enableFurnace,
					atom.recipe.furnace.out_item_id,
					atom.recipe.furnace.output_amount,
					recipeKey,
					Blocks.getBlock(blockGoc(recipeKey))
				);
			}

		}
	}

	public void createItemRecipes(List<CompiledItem> atoms) {
		for (CompiledItem atom : atoms) {
			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) {
				Main.LOGGER.warn("Wrong format version for item '{}'.", atom.data.name);
				continue;
			}

			String recipeKey = atom.meta.author + "_" + atom.data.name;

			if (atom.recipe == null ) continue;

			// Workbench recipe
			if (atom.recipe.workbench != null ) {
				createWorkbenchRecipe(
					atom.recipe.enableWorkbench,
					atom.recipe.workbench.pattern,
					atom.recipe.workbench.symbols,
					atom.recipe.workbench.outputAmount,
					recipeKey,
					new ItemStack(Item.getItem(itemGoc(recipeKey)), atom.recipe.workbench.outputAmount)
				);
			}

			// Furnace recipe
			if (atom.recipe.furnace != null ) {
				createFurnaceRecipe(
					atom.recipe.enableFurnace,
					atom.recipe.furnace.out_item_id,
					atom.recipe.furnace.output_amount,
					recipeKey,
					Item.getItem(itemGoc(recipeKey))
				);
			}
		}
	}

	private void createWorkbenchRecipe(
		boolean enabled,
		List<List<String>> pattern,
		List<java.util.Map<String, Integer>> symbols,
		int outputAmount,
		String recipeKey,
		ItemStack output
	) {
		if (!enabled || outputAmount <= 0 || pattern.isEmpty()) {
			return;
		}

		String[] shape = pattern.stream()
			.map(row -> String.join("", row))
			.toArray(String[]::new);

		RecipeBuilderShaped recipeBuilderShaped = RecipeBuilder.Shaped(MOD_ID)
			.setShape(shape);

		symbols.forEach(symbolMap -> {
			symbolMap.forEach((k, v) -> {
				recipeBuilderShaped.addInput(k.charAt(0), Item.getItem(v));
			});
		});

		recipeBuilderShaped.create(MOD_ID + ":" + recipeKey, output);
	}

	private void createFurnaceRecipe(
		boolean enabled,
		int outItemId,
		int outputAmount,
		String recipeKey,
		Object input
	) {
		if (!enabled || outItemId <= 0) {
			return;
		}

		RecipeBuilderFurnace recipeBuilderFurnace = new RecipeBuilderFurnace(MOD_ID)
			.setInput((IItemConvertible)input);

		recipeBuilderFurnace.create(
			MOD_ID + ":" + recipeKey,
			new ItemStack(outItemId, outputAmount, 0)
		);
	}
}
