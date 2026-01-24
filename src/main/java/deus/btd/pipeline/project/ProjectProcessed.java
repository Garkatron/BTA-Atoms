package deus.btd.pipeline.project;

import deus.btd.generation.AtomBiome;
import deus.btd.pipeline.compile.types.AtomType;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.*;

public final class ProjectProcessed {

	private final String name;
	private final ProjectResources resources;
	private final Map<String, Block<?>> blocks;
	private final Map<String, Item> items;
	private final Map<String, AtomBiome> biomes;
	public final Map<AtomType, List<?>> atoms;


	public ProjectProcessed(
		String name,
		ProjectResources resources,
		Map<String, Block<?>> blocks,
		Map<String, Item> items,
		Map<String, AtomBiome> biomes,
		Map<AtomType, List<?>> atoms
	) {
		this.name = name;
		this.resources = resources;
		this.blocks = new HashMap<>(blocks);
		this.items = new HashMap<>(items);
		this.biomes = biomes;
		this.atoms = atoms;

	}

	public String name() { return name; }
	public ProjectResources resources() { return resources; }
	public Map<String, Block<?>> blocks() { return blocks; }
	public Map<String, Item> items() { return items; }

	public Map<String, AtomBiome> getBiomes() {
		return biomes;
	}
}
