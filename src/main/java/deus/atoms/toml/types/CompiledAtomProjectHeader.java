package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;

import java.util.List;

@DeserializeToml
public class CompiledAtomProjectHeader extends CompiledAtom {

	public Data data;

	@DeserializeToml
	public static class Data {
		public String name;
	}

}
