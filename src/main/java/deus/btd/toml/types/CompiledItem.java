package deus.btd.toml.types;

import deus.btd.annotations.DeserializeToml;
import deus.btd.interfaces.IHasLang;
import deus.btd.toml.types.fields.Lang;
import deus.btd.toml.types.fields.Recipe;

import javax.annotation.Nullable;
import java.util.List;

@DeserializeToml
public class CompiledItem extends CompiledAtom  implements IHasLang {

	public Data data;
	public Textures textures;

	@Nullable public Lang lang;
	@Nullable public Recipe recipe;
	@Nullable public Tool tool;

	@Override
	public @Nullable Lang getLang() {
		return lang;
	}

	@Override
	public String Namespace() {
		return this.namespace;
	}

	@DeserializeToml
	public static class Data {
		public String name;
		public List<String> tags;
		public String material;
		public int maxStackSize;
	}

	@DeserializeToml
	public static class Textures {
		public String texture;
	}

	@DeserializeToml
	public static class Tool {
		public String type;
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
