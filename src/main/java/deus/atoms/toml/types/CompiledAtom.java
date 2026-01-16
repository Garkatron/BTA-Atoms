package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;
import deus.atoms.annotations.IHasLang;
import deus.atoms.annotations.IHasMeta;

import java.util.Map;

@DeserializeToml
public class CompiledAtom implements IHasMeta, IHasLang {
	public Meta meta;
	public Lang lang;

	@Override
	public Lang getLang() {
		return lang;
	}

	@Override
	public Meta getMeta() {
		return meta;
	}

	@DeserializeToml
	public static class Meta {
		public String author;
		public int formatVersion;
	}

	@DeserializeToml
	public static class Lang { // [lang]
		public String key;
		public Map<String, LangLocale> locales; // [lang.en_US], [lang.es_ES] como Map


		@DeserializeToml
		public static class LangLocale {
			public String name;
			public String desc;
			public String tooltip;
		}
	}


}
