package deus.btd.generation;

import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.weather.Weather;

import java.util.ArrayList;
import java.util.List;

public class AtomBiome extends Biome {

	public AtomBiome(String key) {
		super(key);
	}


	public AtomBiome color(int color) {
		this.color = color;
		return this;
	}

	public AtomBiome topBlock(short block) {
		this.topBlock = block;
		return this;
	}

	public AtomBiome fillerBlock(short block) {
		this.fillerBlock = block;
		return this;
	}

	public AtomBiome blockedWeathers(Weather... weathers) {
		this.blockedWeathers = weathers;
		return this;
	}

	/*
	public AtomBiome hasSurfaceSnow(boolean value) {

		this.hasSurfaceSnow = value;
		return this;
	}
	*/

	/* ---------- spawn lists ---------- */

	public AtomBiome monsters(List<SpawnListEntry> list) {
		this.spawnableMonsterList = list;
		return this;
	}

	public AtomBiome creatures(List<SpawnListEntry> list) {
		this.spawnableCreatureList = list;
		return this;
	}

	public AtomBiome waterCreatures(List<SpawnListEntry> list) {
		this.spawnableWaterCreatureList = list;
		return this;
	}

	public AtomBiome ambientCreatures(List<SpawnListEntry> list) {
		this.spawnableAmbientCreatureList = list;
		return this;
	}

	/* ---------- helpers cómodos ---------- */

	public AtomBiome addMonster(SpawnListEntry entry) {
		if (this.spawnableMonsterList == null) {
			this.spawnableMonsterList = new ArrayList<>();
		}
		this.spawnableMonsterList.add(entry);
		return this;
	}

	public AtomBiome addCreature(SpawnListEntry entry) {
		if (this.spawnableCreatureList == null) {
			this.spawnableCreatureList = new ArrayList<>();
		}
		this.spawnableCreatureList.add(entry);
		return this;
	}

	public AtomBiome addWaterCreature(SpawnListEntry entry) {
		if (this.spawnableWaterCreatureList == null) {
			this.spawnableWaterCreatureList = new ArrayList<>();
		}
		this.spawnableWaterCreatureList.add(entry);
		return this;
	}

	public AtomBiome addAmbientCreature(SpawnListEntry entry) {
		if (this.spawnableAmbientCreatureList == null) {
			this.spawnableAmbientCreatureList = new ArrayList<>();
		}
		this.spawnableAmbientCreatureList.add(entry);
		return this;
	}
}
