package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;
import deus.atoms.toml.types.fields.Recipe;

import javax.annotation.Nullable;
import java.util.List;

@DeserializeToml
public class CompiledItem extends CompiledAtom {
	public Data data;
	public Textures textures;
	@Nullable public Recipe recipe;

	@DeserializeToml
	public static class Data {
		public String name;
		public List<String> tags;
		public String material;
		public int maxDamage;
		public int maxStackSize;
	}

	@DeserializeToml
	public static class Textures {
		public String encoding;
		public String texture;
	}

	// Optional
	@Nullable
	public Tool tool;

	@DeserializeToml
	public static class Tool {
		public String type;
		public int damageDealt;
		public String toolMaterial;
		public String material;
		public int weaponDamage;
	}

	// Optional
	@Nullable
	public Food food;

	@DeserializeToml
	public static class Food {
		public int healAmount;
		public int ticksPerHeal;
		public boolean favouriteWolfMeat;
	}
}
