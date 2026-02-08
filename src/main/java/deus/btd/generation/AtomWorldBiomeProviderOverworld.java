package deus.btd.generation;

import deus.btd.Main;
import deus.btd.pipeline.compile.types.CompiledWorld;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.data.BiomeRange;
import net.minecraft.core.world.biome.data.BiomeRangeMap;
import net.minecraft.core.world.biome.provider.BiomeProviderOverworld;
import net.minecraft.core.world.noise.PerlinSimplexNoise;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.util.helper.MathHelper;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AtomWorldBiomeProviderOverworld extends BiomeProvider {
	private final BiomeRangeMap brm = new BiomeRangeMap();
	private PerlinSimplexNoise temperatureNoise;
	private PerlinSimplexNoise humidityNoise;
	private PerlinSimplexNoise varietyNoise;
	private PerlinSimplexNoise fuzzinessNoise;

	private NoiseSettings temperature;
	private NoiseSettings humidity;
	private NoiseSettings variety;
	private NoiseSettings fuzziness;

	private final WorldType worldType;

	private static final long TEMPERATURE_SEED_SALT = 9871L;
	private static final long HUMIDITY_SEED_SALT    = 39811L;
	private static final long VARIETY_SEED_SALT     = 132897987541L;
	private static final long FUZZINESS_SEED_SALT   = 543321L;


	public AtomWorldBiomeProviderOverworld(long seed, WorldType worldType) {
		this.worldType = worldType;

		this.temperatureNoise =
			new PerlinSimplexNoise(new Random(seed * TEMPERATURE_SEED_SALT), 4);
		this.humidityNoise =
			new PerlinSimplexNoise(new Random(seed * HUMIDITY_SEED_SALT), 4);
		this.varietyNoise =
			new PerlinSimplexNoise(new Random(seed * VARIETY_SEED_SALT), 4);
		this.fuzzinessNoise =
			new PerlinSimplexNoise(new Random(seed * FUZZINESS_SEED_SALT), 2);

		this.temperature = new NoiseSettings(0.0125, 0.0125, 0.25, 0.01);
		this.humidity    = new NoiseSettings(0.025,  0.025,  0.3,  0.01);
		this.variety     = new NoiseSettings(0.25,   0.25,   0.3,  0.0);
		this.fuzziness   = new NoiseSettings(0.25,   0.25,   1.0,  0.0);
	}

	public AtomWorldBiomeProviderOverworld withTemperature(NoiseSettings settings) {
		this.temperature = settings;
		return this;
	}

	public AtomWorldBiomeProviderOverworld withHumidity(NoiseSettings settings) {
		this.humidity = settings;
		return this;
	}

	public AtomWorldBiomeProviderOverworld withVariety(NoiseSettings settings) {
		this.variety = settings;
		return this;
	}

	public AtomWorldBiomeProviderOverworld withFuzziness(NoiseSettings settings) {
		this.fuzziness = settings;
		return this;
	}
	public void configureNoise(
		PerlinSimplexNoise temp,
		PerlinSimplexNoise hum,
		PerlinSimplexNoise var,
		PerlinSimplexNoise fuzz,
		NoiseSettings tempSettings,
		NoiseSettings humSettings,
		NoiseSettings varSettings,
		NoiseSettings fuzzSettings
	) {
		this.temperatureNoise = temp;
		this.humidityNoise = hum;
		this.varietyNoise = var;
		this.fuzzinessNoise = fuzz;

		this.temperature = tempSettings;
		this.humidity = humSettings;
		this.variety = varSettings;
		this.fuzziness = fuzzSettings;
	}


	public void configure(Map<String, CompiledWorld.Range> ranges) {
		if (ranges == null || ranges.isEmpty()) {
			Main.LOGGER.warn("No biome ranges to configure");
			return;
		}

		ranges.forEach((b, range) -> {
			String biomeName = b.replace("_", ".").replace("-", ":");
			CompiledWorld.Axis temperature = range.axis.get("temperature");
			CompiledWorld.Axis humidity = range.axis.get("humidity");
			CompiledWorld.Axis erosion = range.axis.get("erosion");
			CompiledWorld.Axis weirdness = range.axis.get("weirdness");

			if (temperature == null || humidity == null || erosion == null || weirdness == null) {
				Main.LOGGER.warn("Biome {} tiene ejes incompletos", biomeName);
				return;
			}

			try {
				Biome biome = Registries.BIOMES.getItem(biomeName);
				if (biome == null) {
					Main.LOGGER.warn("Biome '{}' no encontrado", biomeName);
					return;
				}

				BiomeRange biomeRange = new BiomeRange(
					temperature.min, temperature.max,
					humidity.min, humidity.max,
					erosion.min, erosion.max,
					weirdness.min, weirdness.max
				);

				brm.addRange(biome, new BiomeRange[]{biomeRange});
				Main.LOGGER.info("Added biome range for: {}", biomeName);
			} catch (Exception e) {
				Main.LOGGER.error("Error adding biome range for '{}'", biomeName, e);
			}
		});

		brm.lock();
		Main.LOGGER.info("BiomeProvider configured and locked");
	}

	public void addBiomeRange(Biome biome, BiomeRange... ranges) {
		brm.addRange(biome, ranges);
	}

	public void lock() {
		brm.lock();
	}

	@Override
	public Biome[] getBiomes(Biome[] biomes, double[] temperatures, double[] humidities,
							 double[] varieties, int x, int y, int z, int xSize, int ySize, int zSize) {
		if (biomes == null || biomes.length < xSize * ySize * zSize) {
			biomes = new Biome[xSize * ySize * zSize];
		}

		if (temperatures == null || temperatures.length < xSize * zSize) {
			temperatures = this.getTemperatures(temperatures, x, z, xSize, zSize);
		}

		if (humidities == null || humidities.length < xSize * zSize) {
			humidities = this.getHumidities(humidities, x, z, xSize, zSize);
		}

		if (varieties == null || varieties.length < xSize * zSize) {
			varieties = this.getVarieties(varieties, x, z, xSize, zSize);
		}

		for(int dx = 0; dx < xSize; ++dx) {
			for(int dz = 0; dz < zSize; ++dz) {
				double temperature = temperatures[dx * zSize + dz];
				double humidity = humidities[dx * zSize + dz];
				double variety = varieties[dx * zSize + dz];

				for(int dy = 0; dy < ySize; ++dy) {
					double altitude = this.worldType.getYPercentage(y + dy << 3);
					biomes[dy * xSize * zSize + dz * xSize + dx] =
						this.lookupBiome(temperature, humidity, altitude, variety);
				}
			}
		}

		return biomes;
	}

	@Override
	public double[] getTemperatures(double[] out, int x, int z, int xSize, int zSize) {
		if (out == null || out.length < xSize * zSize) {
			out = new double[xSize * zSize];
		}

		double[] tempNoise = temperatureNoise.getValue(
			null, x, z, xSize, zSize,
			temperature.xScale,
			temperature.zScale,
			temperature.exponent
		);

		double[] fuzzNoise = fuzzinessNoise.getValue(
			null, x, z, xSize, zSize,
			fuzziness.xScale,
			fuzziness.zScale,
			fuzziness.exponent
		);

		for (int i = 0; i < out.length; i++) {
			double fuzz = fuzzNoise[i] * 1.1 + 0.5;
			double valPct = 1.0 - temperature.fuzzPercentage;

			double t = (tempNoise[i] * 0.15 + 0.7) * valPct
				+ fuzz * temperature.fuzzPercentage;

			out[i] = MathHelper.clamp(t, 0.0, 1.0);
		}

		return out;
	}

	@Override
	public double[] getHumidities(double[] out, int x, int z, int xSize, int zSize) {
		if (out == null || out.length < xSize * zSize) {
			out = new double[xSize * zSize];
		}

		double[] humNoise = humidityNoise.getValue(
			null, x, z, xSize, zSize,
			humidity.xScale,
			humidity.zScale,
			humidity.exponent
		);

		double[] fuzzNoise = fuzzinessNoise.getValue(
			null, x, z, xSize, zSize,
			fuzziness.xScale,
			fuzziness.zScale,
			fuzziness.exponent
		);

		for (int i = 0; i < out.length; i++) {
			double fuzz = fuzzNoise[i] * 1.1 + 0.5;
			double valPct = 1.0 - humidity.fuzzPercentage;

			double h = (humNoise[i] * 0.15 + 0.5) * valPct
				+ fuzz * humidity.fuzzPercentage;

			out[i] = MathHelper.clamp(h, 0.0, 1.0);
		}

		return out;
	}


	@Override
	public double[] getVarieties(double[] out, int x, int z, int xSize, int zSize) {
		if (out == null || out.length < xSize * zSize) {
			out = new double[xSize * zSize];
		}

		double[] varNoise = varietyNoise.getValue(
			null, x, z, xSize, zSize,
			variety.xScale,
			variety.zScale,
			variety.exponent
		);

		double[] fuzzNoise = fuzzinessNoise.getValue(
			null, x, z, xSize, zSize,
			fuzziness.xScale,
			fuzziness.zScale,
			fuzziness.exponent
		);

		for (int i = 0; i < out.length; i++) {
			double fuzz = fuzzNoise[i] * 1.1 + 0.5;
			double valPct = 1.0 - variety.fuzzPercentage;

			double v = (varNoise[i] * 0.15 + 0.5) * valPct
				+ fuzz * variety.fuzzPercentage;

			out[i] = MathHelper.clamp(v, 0.0, 1.0);
		}

		return out;
	}

	@Override
	public double[] getBiomenesses(double[] biomenesses, int x, int y, int z,
								   int xSize, int ySize, int zSize) {
		if (biomenesses == null || biomenesses.length < xSize * ySize * zSize) {
			biomenesses = new double[xSize * ySize * zSize];
		}

		double[] temperatures = this.getTemperatures(null, x, z, xSize, zSize);
		double[] humidities = this.getHumidities(null, x, z, xSize, zSize);
		double[] varieties = this.getVarieties(null, x, z, xSize, zSize);

		for(int dx = 0; dx < xSize; ++dx) {
			for(int dy = 0; dy < ySize; ++dy) {
				for(int dz = 0; dz < zSize; ++dz) {
					double temperature = MathHelper.clamp(temperatures[dx * zSize + dz], 0.0, 1.0);
					double humidity = MathHelper.clamp(humidities[dx * zSize + dz], 0.0, 1.0);
					double altitude = MathHelper.clamp(this.worldType.getYPercentage(y + dy << 3), 0.0, 1.0);
					double variety = MathHelper.clamp(varieties[dx * zSize + dz], 0.0, 1.0);

					Biome biome = this.lookupBiome(temperature, humidity, altitude, variety);
					Set<BiomeRange> ranges = brm.getRanges(biome);
					humidity *= temperature;
					double biomeness = 0.0;

					for(BiomeRange range : ranges) {
						if (range.contains(temperature, humidity, variety, altitude)) {
							double temperatureRange = range.getMaxTemperature() - range.getMinTemperature();
							double humidityRange = range.getMaxHumidity() - range.getMinHumidity();
							double altitudeRange = range.getMaxAltitude() - range.getMinAltitude();
							double varietyRange = range.getMaxVariety() - range.getMinVariety();

							double newTemperature = (temperature - range.getMinTemperature()) / temperatureRange;
							double newHumidity = (humidity - range.getMinHumidity()) / humidityRange;
							double newAltitude = (altitude - range.getMinAltitude()) / altitudeRange;
							double newVariety = (variety - range.getMinVariety()) / varietyRange;

							if ((!(range.getMinTemperature() <= 0.0) || !(newTemperature <= 0.5)) &&
								(!(range.getMaxTemperature() >= 1.0) || !(newTemperature >= 0.5))) {
								newTemperature = -Math.abs(newTemperature * 2.0 - 1.0) + 1.0;
							} else {
								newTemperature = 1.0;
							}

							if ((!(range.getMinHumidity() <= 0.0) || !(newHumidity <= 0.5)) &&
								(!(range.getMaxHumidity() >= 1.0) || !(newHumidity >= 0.5))) {
								newHumidity = -Math.abs(newHumidity * 2.0 - 1.0) + 1.0;
							} else {
								newHumidity = 1.0;
							}

							if ((!(range.getMinAltitude() <= 0.0) || !(newAltitude <= 0.5)) &&
								(!(range.getMaxAltitude() >= 1.0) || !(newAltitude >= 0.5))) {
								newAltitude = -Math.abs(newAltitude * 2.0 - 1.0) + 1.0;
							} else {
								newAltitude = 1.0;
							}

							if ((!(range.getMinVariety() <= 0.0) || !(newVariety <= 0.5)) &&
								(!(range.getMaxVariety() >= 1.0) || !(newVariety >= 0.5))) {
								newVariety = -Math.abs(newVariety * 2.0 - 1.0) + 1.0;
							} else {
								newVariety = 1.0;
							}

							double newBiomeness = newTemperature * newHumidity * newAltitude * newVariety;
							if (newBiomeness > biomeness) {
								biomeness = newBiomeness;
							}
						}
					}

					biomenesses[dy * xSize * zSize + dz * xSize + dx] = biomeness;
				}
			}
		}

		return biomenesses;
	}

	@Override
	public Biome lookupBiome(double temperature, double humidity, double variety, double altitude) {
		humidity *= temperature;
		return brm.lookupBiome(temperature, humidity, variety, altitude);
	}
}
