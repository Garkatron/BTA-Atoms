package deus.btd.generation;

import deus.btd.pipeline.registry.BiomeConfigRegistry;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.type.overworld.WorldTypeOverworld;

public class AtomWorldTypeOverworld extends WorldTypeOverworld {

	private final String worldKey;

	public AtomWorldTypeOverworld(Properties properties, String worldKey) {
		super(properties);
		this.worldKey = worldKey;
	}

	@Override
	public BiomeProvider createBiomeProvider(World world) {
		AtomWorldBiomeProviderOverworld provider = new AtomWorldBiomeProviderOverworld(world.getRandomSeed(), this);

		provider.configure(BiomeConfigRegistry.get(worldKey));

		return provider;
	}
}
