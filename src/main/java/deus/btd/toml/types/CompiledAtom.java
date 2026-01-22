package deus.btd.toml.types;

import deus.btd.annotations.DeserializeToml;
import deus.btd.interfaces.IHasMeta;
import deus.btd.annotations.TomlIgnoreField;

@DeserializeToml
public class CompiledAtom implements IHasMeta {

	@TomlIgnoreField public String namespace;

	public Meta meta;

	@Override
	public Meta getMeta() {
		return meta;
	}

	@DeserializeToml
	public static class Meta {
		public String author;
		public int formatVersion;
	}
}
