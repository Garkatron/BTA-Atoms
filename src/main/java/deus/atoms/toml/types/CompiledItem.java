package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;

import java.util.List;

@DeserializeToml
public class CompiledItem extends CompiledAtom {
	public Data data;
	public Textures textures;

	@DeserializeToml
	public static class Data {
		public String name;
		public List<String> tags;
		public String material;
		public int maxDamage;
		public int maxStackSize;
	}

	@DeserializeToml
	public static class Textures {
		public String encoding;
		public String texture;
	}


}
