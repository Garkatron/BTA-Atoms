package deus.btd.pipeline.registry;

import deus.btd.pipeline.compile.types.CompiledWorld;
import java.util.HashMap;
import java.util.Map;

public class BiomeConfigRegistry {
    private static final Map<String, Map<String, CompiledWorld.Range>> CONFIGS = new HashMap<>();

    public static void register(String worldKey, Map<String, CompiledWorld.Range> ranges) {
        CONFIGS.put(worldKey, ranges);
    }

    public static Map<String, CompiledWorld.Range> get(String worldKey) {
        return CONFIGS.get(worldKey);
    }
}
