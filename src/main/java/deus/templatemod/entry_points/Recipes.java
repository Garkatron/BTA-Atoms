package deus.templatemod.entry_points;

import deus.templatemod.Main;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class Recipes implements RecipeEntrypoint {

	public static final RecipeNamespace PAPERWORK_RECIPE_NAMESPACE = new RecipeNamespace();

	@Override
	public void initNamespaces() {
		Registries.RECIPES.register(Main.MOD_ID, PAPERWORK_RECIPE_NAMESPACE);
	}

	@Override
	public void onRecipesReady() {



//		RecipeBuilder.Shaped(MOD_ID)
//			.setShape(
//				"IWI",
//				"SWS",
//				"I I")
//			.addInput('I', Items.INGOT_IRON)
//			.addInput('S', Items.STICK)
//			.addInput('W', Blocks.WOOL)
//			.create(MOD_ID+":pencil", PaperworkItems.OFFICE_CHAIR);

//
//		RecipeGroup group = PAPERWORK_RECIPE_NAMESPACE.getItem("printer");
//		if (group == null) {
//			return;
//		}
//
//		for (Object recipe : group.getAllRecipes()) {
//			System.out.println("Receta: " + group.getKey(recipe));
//
//		}
	}
}
