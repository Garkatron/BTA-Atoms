package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DeserializeToml
public class CompiledWorld extends CompiledAtom {

	public Data data;
	public Map<String, Range> ranges;
	public Properties properties;


	@DeserializeToml
	public static class Data {
		public String key;
	}

	@DeserializeToml
	public static class Properties {

		public String languageKey;

		public int defaultWeatherId;

		public boolean hasCeiling = false;

		public List<Float> brightnessRamp = new ArrayList<>();

		public int minY = 0;
		public Integer minPortalY = null;

		public int maxY = 255;
		public Integer maxPortalY = null;

		public int oceanY = 128;

		public int oceanBlockId = 0;
		public int fillerBlockId = 0;

		public int dayNightCycleTicks = 24000;

		public boolean mayRespawn = false;
		public boolean isRetro = false;
	}


	@DeserializeToml
	public static class Range {
		public Map<String, Axis> axis;
	}


	@DeserializeToml
	public static class Axis {
		public double min;
		public double max;
	}

}
