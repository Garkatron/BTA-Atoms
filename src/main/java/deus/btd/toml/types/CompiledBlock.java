package deus.btd.toml.types;

import deus.btd.annotations.DeserializeToml;
import deus.btd.interfaces.IHasLang;
import deus.btd.toml.types.fields.Lang;
import deus.btd.toml.types.fields.Recipe;

import javax.annotation.Nullable;
import java.util.List;

@DeserializeToml
public class CompiledBlock extends CompiledAtom implements IHasLang {

	public Data data;
	public Textures textures;
	@Nullable
	public Sounds sounds;
	@Nullable
	public Flammability flammability;
	@Nullable
	public Render render;
	@Nullable
	public Physics physics;
	@Nullable public Events events;
	@Nullable public Recipe recipe;
	@Nullable public Model model;
	@Nullable public Logic logic;
	@Nullable public Lang lang;

	@Override
	public @Nullable Lang getLang() {
		return lang;
	}

	@Override
	public String Namespace() {
		return this.namespace;
	}

	// ------------------- Inner classes -------------------

	@DeserializeToml
	public static class Data {
		public String name;
		@Nullable
		public List<String> tags;
		public boolean immovable;
		public boolean unbreakable;
		public double resistance;
		public double slipperiness;
		public double hardness;
		public String material;
		public double luminance;
	}

	@DeserializeToml
	public static class Model {
		public String type;
		public int baseBlockId; // Only Stairs
		@Nullable public String rootKey; // Only Chests
	}

	@DeserializeToml
	public static class Logic {
		public String type;
	}

	@DeserializeToml
	public static class Flammability {
		public int chanceToCatchFire;
		public int changeToDegrade;
	}

	@DeserializeToml
	public static class Render {
		public boolean isCubeShaped;
		public boolean isSolidRender;
	}

	@DeserializeToml
	public static class Sounds {
		public String sound;
	}

	@DeserializeToml
	public static class Physics {
		public boolean isCollidable;
	}

	@DeserializeToml
	public static class Textures {
		public Faces faces;

		@DeserializeToml
		public static class Faces {
			public String top;
			public String bottom;
			public String north;
			public String south;
			public String west;
			public String east;
		}
	}

	@DeserializeToml
	public static class Events {
		public BreakEvent onBreak;

		@DeserializeToml
		public static class BreakEvent {
			public boolean dropItself;
			public List<Drops> drops;

			@DeserializeToml
			public static class Drops {
				public int item;
				public String cause;
				public int chance;
			}
		}
	}



}
