package deus.btd.entry_points;

import deus.btd.Main;
import deus.btd.toml.AtomCompiler;
import deus.btd.toml.project.ProjectProcessed;
import deus.btd.toml.types.AtomType;
import deus.btd.toml.types.CompiledBlock;
import deus.btd.toml.types.CompiledItem;
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

import static deus.btd.Main.LOGGER;
import static deus.btd.Main.MOD_ID;
import static deus.btd.toml.AtomLoader.formatAtomKey;
import static deus.btd.utils.ConfigManager.blockGoc;
import static deus.btd.utils.ConfigManager.itemGoc;


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
			List<CompiledBlock> blocks = (List<CompiledBlock>) project.resources.getAtoms().get(AtomType.BLOCK);
			if (blocks != null && !blocks.isEmpty()) {
				createBlockRecipes(blocks);
			}

			List<CompiledItem> items = (List<CompiledItem>) project.resources.getAtoms().get(AtomType.ITEM);
			if (items != null && !items.isEmpty()) {
				createItemRecipes(items);
			}
		}
	}

	public static String formatRecipeKey(String author, String name, String type) {
		return formatAtomKey(author, name) + "_" + type;
	}

	public void createBlockRecipes(List<CompiledBlock> atoms) {
		for (CompiledBlock atom : atoms) {
			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) {
				Main.LOGGER.warn("Wrong format version for block '{}' v{}.", atom.data.name, atom.meta.formatVersion);
				continue;
			}

			if (atom.recipe == null ) continue;

			String key = formatAtomKey(atom.meta.author, atom.data.name);

			if (atom.recipe.workbench != null ) {
				createWorkbenchRecipe(
					atom.recipe.enableWorkbench,
					atom.recipe.workbench.pattern,
					atom.recipe.workbench.symbols,
					atom.recipe.workbench.outputAmount,
					formatRecipeKey(atom.meta.author, atom.data.name, "workbench"),
					new ItemStack(Blocks.getBlock(blockGoc(key)).asItem(), atom.recipe.workbench.outputAmount)
				);
			}

			if(atom.recipe.furnace != null) {
				createFurnaceRecipe(
					atom.recipe.enableFurnace,
					atom.recipe.furnace.out_item_id,
					atom.recipe.furnace.output_amount,
					formatRecipeKey(atom.meta.author, atom.data.name, "furnace"),
					Blocks.getBlock(blockGoc(key))
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

			String key = formatAtomKey(atom.meta.author, atom.data.name);

			if (atom.recipe == null ) continue;

			if (atom.recipe.workbench != null ) {
				createWorkbenchRecipe(
					atom.recipe.enableWorkbench,
					atom.recipe.workbench.pattern,
					atom.recipe.workbench.symbols,
					atom.recipe.workbench.outputAmount,
					formatRecipeKey(atom.meta.author, atom.data.name, "workbench"),
					new ItemStack(Item.getItem(itemGoc(key)), atom.recipe.workbench.outputAmount)
				);
			}

			if (atom.recipe.furnace != null ) {
				createFurnaceRecipe(
					atom.recipe.enableFurnace,
					atom.recipe.furnace.out_item_id,
					atom.recipe.furnace.output_amount,
					formatRecipeKey(atom.meta.author, atom.data.name, "furnace"),
					Item.getItem(itemGoc(key))
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
