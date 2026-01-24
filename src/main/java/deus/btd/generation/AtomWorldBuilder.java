package deus.btd.generation;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.weather.WeatherManager;

public class AtomWorldBuilder {

	private final AtomWorld world;

	public AtomWorldBuilder() {
		this.world = new AtomWorld();
	}

	public AtomWorldBuilder dimension(Dimension dimension) {
		world.dimension = dimension;
		return this;
	}

	public AtomWorldBuilder worldType(WorldType worldType) {
		world.worldType = worldType;
		return this;
	}

	public AtomWorldBuilder chunkProvider(IChunkProvider provider) {
		world.chunkProvider = provider;
		return this;
	}

	public AtomWorldBuilder saveHandler(LevelStorage storage) {
		world.saveHandler = storage;
		return this;
	}

	public AtomWorldBuilder biomeProvider(BiomeProvider provider) {
		world.biomeProvider = provider;
		return this;
	}

	public AtomWorldBuilder seasonManager(SeasonManager manager) {
		world.seasonManager = manager;
		return this;
	}

	public AtomWorldBuilder weatherManager(WeatherManager manager) {
		world.weatherManager = manager;
		return this;
	}

	public AtomWorldBuilder clientSide(boolean clientSide) {
		world.isClientSide = clientSide;
		return this;
	}

	public AtomWorld build() {
		if (world.dimension == null) {
			throw new IllegalStateException("Dimension no puede ser null");
		}
		return world;
	}
}
