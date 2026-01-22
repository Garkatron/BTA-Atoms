package deus.btd.toml.project;

import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.*;

public class ProjectProcessed {
	public final String name;
	public final ProjectResources resources;
	public final Map<String, Block<?>> blocks;
	public final Map<String, Item> items;

	public ProjectProcessed(String name, ProjectResources resources, Map<String, Block<?>> blocks, Map<String, Item> items) {
		this.name = name;
		this.blocks = blocks;
		this.items = items;
		this.resources = resources;
	}

	public String getName() {
		return name;
	}

	public Map<String, Block<?>> getBlocks() {
		return blocks;
	}

	public Map<String, Item> getItems() {
		return items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectProcessed that = (ProjectProcessed) o;
		return Objects.equals(name, that.name) &&
			Objects.equals(blocks, that.blocks) &&
			Objects.equals(items, that.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, blocks, items);
	}

	@Override
	public String toString() {
		return "ProjectProcessed{" +
			"name='" + name + '\'' +
			", blocks=" + blocks +
			", items=" + items +
			'}';
	}
}
