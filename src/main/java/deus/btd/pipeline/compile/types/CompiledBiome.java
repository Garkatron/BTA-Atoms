package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;

import java.util.List;

@DeserializeToml
public class CompiledBiome extends CompiledAtom {
	public Data data;

	public static class Data {
		public String key;
		public boolean hasSurfaceSnow;
		public int color;
		public List<String> blockedWeathers;
	}
}
