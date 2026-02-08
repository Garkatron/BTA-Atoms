package deus.btd.generation;

import deus.btd.pipeline.compile.types.CompiledWorld;
import deus.btd.pipeline.registry.BiomeConfigRegistry;
import deus.btd.pipeline.registry.NoiseConfigRegistry;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.type.overworld.WorldTypeOverworld;

import java.util.function.Supplier;

import static deus.btd.pipeline.build.WorldFactory.buildNoise;
import static deus.btd.pipeline.build.WorldFactory.buildSettings;

public class AtomWorldTypeOverworld extends WorldTypeOverworld {

	private final String worldKey;

	public AtomWorldTypeOverworld(Properties properties, String worldKey) {
		super(properties);
		this.worldKey = worldKey;
	}



	@Override
	public BiomeProvider createBiomeProvider(World world) {
		AtomWorldBiomeProviderOverworld provider =
			new AtomWorldBiomeProviderOverworld(world.getRandomSeed(), this);

		// Biomes
		provider.configure(BiomeConfigRegistry.get(worldKey));

		// Noise
		CompiledWorld.Noises noises =
			NoiseConfigRegistry.get(worldKey); // ahora guarda CompiledWorld.Noises

		if (noises != null) {
			provider.configureNoise(
				buildNoise(world.getRandomSeed(), noises.temperature),
				buildNoise(world.getRandomSeed(), noises.humidity),
				buildNoise(world.getRandomSeed(), noises.variety),
				buildNoise(world.getRandomSeed(), noises.fuzziness),

				buildSettings(noises.temperature),
				buildSettings(noises.humidity),
				buildSettings(noises.variety),
				buildSettings(noises.fuzziness)
			);
		}

		return provider;
	}




}
