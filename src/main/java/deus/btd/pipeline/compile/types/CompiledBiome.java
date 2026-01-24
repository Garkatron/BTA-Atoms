package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;
import deus.btd.pipeline.compile.types.fields.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeserializeToml
public class CompiledBiome extends CompiledAtom {
	public Data data;
	public List<Map<String,Integer>> monster;
	public List<Map<String,Integer>> creature;
	public List<Map<String,Integer>> water;
	public List<Map<String,Integer>> ambient;

	@DeserializeToml
	public static class Data {
		public String key;
		public String world_key;
		public boolean hasSurfaceSnow;
		public int color;
		public short topBlock;
		public short fillerBlock;
		public List<String> blockedWeathers;
	}


}
