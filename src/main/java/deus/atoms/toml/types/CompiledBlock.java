package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeserializeToml
public class CompiledBlock extends CompiledAtom {

	public Data data;
	public Flammability flammability;
	public Render render;
	public Sounds sounds;
	public Physics physics;
	public Textures textures;
	public Events events;
	public Recipe recipe;

	// ------------------- Inner classes -------------------

	@DeserializeToml
	public static class Data {
		public String name;
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
		public String encoding;
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
		public BreakEvent onBreak; // mapear [events.break]

		@DeserializeToml
		public static class BreakEvent {
			public boolean dropItself;
			public List<Drops> drops; // [[events.break.drops]]

			@DeserializeToml
			public static class Drops {
				public int item;
				public String cause;
				public int chance;
			}
		}
	}


	@DeserializeToml
	public static class Recipe {
		public boolean enableWorkbench;
		public boolean enableFurnace;
		public Workbench workbench;
		public Furnace furnace;

		@DeserializeToml
		public static class Workbench {
			public int outputAmount;
			public List<List<String>> pattern;
			public List<Map<String,Integer>> symbols = new ArrayList<>();

		}

		@DeserializeToml
		public static class Furnace {
			public int out_item_id;
			public int output_amount;
		}
	}
}
