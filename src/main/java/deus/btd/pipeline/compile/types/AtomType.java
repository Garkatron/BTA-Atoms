package deus.btd.pipeline.compile.types;

import java.util.Arrays;

public enum AtomType {
	BLOCK(".block.atom"),
	ITEM(".item.atom"),
	BIOME(".biome.atom"),
	WEATHER(".wheather.atom"),
	WORLD(".world.atom"),
	PROJECT(".project.zip");

	private final String suffix;

	AtomType(String suffix) {
		this.suffix = suffix;
	}

	public static AtomType fromFileName(String name) {
		return Arrays.stream(values())
			.filter(t -> name.endsWith(t.suffix))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Tipo de atom desconocido: " + name));
	}

	public String suffix() {
		return suffix;
	}
}
