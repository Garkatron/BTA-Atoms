package deus.btd.toml.types.fields;

import deus.btd.annotations.DeserializeToml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeserializeToml
public class Recipe {
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
