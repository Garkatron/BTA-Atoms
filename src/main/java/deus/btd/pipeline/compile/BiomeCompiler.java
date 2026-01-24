package deus.btd.pipeline.compile;

import deus.btd.pipeline.compile.types.CompiledBiome;
import deus.btd.pipeline.util.AtomConstants;

public class BiomeCompiler {
	public static boolean validate(CompiledBiome biome) {
		if (biome == null) return false;
		if (biome.meta.formatVersion != AtomConstants.FORMAT_VERSION) return false;
		return biome.data.key != null;
	}
}
