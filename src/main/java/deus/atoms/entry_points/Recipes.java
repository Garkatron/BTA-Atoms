package deus.atoms.entry_points;

import deus.atoms.AtomCompiler;
import deus.atoms.AtomDataCache;
import deus.atoms.Main;
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
		List<TomlParseResult> atoms = AtomDataCache.ATOMS;

		for (TomlParseResult data : atoms) {
			// META
			String author = AtomCompiler.getOrDefault(data, "meta.author", "unknown");
			int version = AtomCompiler.getOrDefault(data, "meta.format_version", 0);

			// DATA
			String atomName = AtomCompiler.getOrDefault(data, "data.name", "Unnamed");

			if (version != AtomCompiler.AtomFormatVersion) {
				Main.LOGGER.warn("Wrong format version for block '{}'.", atomName);
				continue;
			}

			int amount = AtomCompiler.getOrDefault(data, "recipe.workbench.amount", 0);


			// RECIPE

			TomlTable recipe = data.getTable("recipe.workbench");
			if (recipe == null) return;

			TomlArray patternArray = recipe.getArray("pattern");
			if (patternArray == null || patternArray.isEmpty()) return;

			List<String> patternStrings = new ArrayList<>();

			for (int i = 0; i < patternArray.size(); i++) {
				TomlArray row = patternArray.getArray(i);
				if (row == null) continue;

				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < row.size(); j++) {
					String value = row.getString(j);
					if (value != null) sb.append(value);
				}
				patternStrings.add(sb.toString());
			}

			RecipeBuilderShaped recipeBuilderShaped = RecipeBuilder.Shaped(MOD_ID)
				.setShape(patternStrings.get(0), patternStrings.get(1), patternStrings.get(2));

			TomlArray symbolsArray = data.getArrayOrEmpty("recipe.workbench.symbols");
			for (int i = 0; i < symbolsArray.size(); i++) {
				TomlTable symbolTable = symbolsArray.getTable(i);
				if (symbolTable == null) continue;

				for (String key : symbolTable.keySet()) {
					Long itemIdLong = symbolTable.getLong(key);
					if (itemIdLong == null) continue;
					int itemId = itemIdLong.intValue();

					recipeBuilderShaped.addInput(key.charAt(0), Item.getItem(itemId));
				}
			}

			String recipeKey = author + "_" + atomName;
			recipeBuilderShaped.create(MOD_ID + ":" + recipeKey, new ItemStack(Blocks.getBlock(blockGoc(recipeKey)).asItem(), amount));
		}
	}

}
