package deus.btd.pipeline.project;

import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.*;

public final class ProjectProcessed {

	private final String name;
	private final ProjectResources resources;
	private final Map<String, Block<?>> blocks;
	private final Map<String, Item> items;

	public ProjectProcessed(
		String name,
		ProjectResources resources,
		Map<String, Block<?>> blocks,
		Map<String, Item> items
	) {
		this.name = name;
		this.resources = resources;
		this.blocks = new HashMap<>(blocks);
		this.items = new HashMap<>(items);
	}

	public String name() { return name; }
	public ProjectResources resources() { return resources; }
	public Map<String, Block<?>> blocks() { return blocks; }
	public Map<String, Item> items() { return items; }
}
