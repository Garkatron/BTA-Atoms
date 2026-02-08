package deus.btd.pipeline.registry;

import deus.btd.pipeline.compile.types.CompiledWorld;

import java.util.HashMap;
import java.util.Map;

public final class NoiseConfigRegistry {
	private static final Map<String, CompiledWorld.Noises> REGISTRY = new HashMap<>();

	public static void register(String worldKey, CompiledWorld.Noises config) {
		REGISTRY.put(worldKey, config);
	}

	public static CompiledWorld.Noises get(String worldKey) {
		return REGISTRY.get(worldKey);
	}
}
