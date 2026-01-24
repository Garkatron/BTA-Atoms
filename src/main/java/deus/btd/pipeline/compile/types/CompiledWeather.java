package deus.btd.pipeline.compile.types;

import deus.btd.annotations.DeserializeToml;

@DeserializeToml
public class CompiledWeather extends CompiledAtom {

	public Data data;

	@DeserializeToml
	public static class Data {
		public boolean isPrecipitation;
		public int subtractLightLevel;
		public int precipitationType;
		public boolean spawnRainParticles;
		public boolean isDamp;
		public float fogDistance;
	}
}
