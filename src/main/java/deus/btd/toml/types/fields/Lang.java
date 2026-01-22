package deus.btd.toml.types.fields;

import deus.btd.annotations.DeserializeToml;

import java.util.Map;

@DeserializeToml
public class Lang { // [lang]
	public String key;
	public Map<String, LangLocale> locales; // [lang.en_US], [lang.es_ES] como Map


	@DeserializeToml
	public static class LangLocale {
		public String name;
		public String desc;
		public String tooltip;
	}
}
