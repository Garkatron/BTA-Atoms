package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;

@DeserializeToml
public class CompiledAtomProjectHeader extends CompiledAtom {

	public Data data;

	@DeserializeToml
	public static class Data {
		public String name;
	}

}
