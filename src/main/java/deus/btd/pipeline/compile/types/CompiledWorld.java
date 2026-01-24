package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;

import java.util.Map;

@DeserializeToml
public class CompiledWorld extends CompiledAtom {

	public Map<String, Range> ranges;

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
