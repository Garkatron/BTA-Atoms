package deus.btd.pipeline.project;

import deus.btd.Main;
import deus.btd.generation.AtomBiome;
import deus.btd.generation.AtomWorld;
import deus.btd.pipeline.build.BiomeFactory;
import deus.btd.pipeline.build.BlockFactory;
import deus.btd.pipeline.build.ItemFactory;
import deus.btd.pipeline.build.WorldFactory;
import deus.btd.pipeline.compile.types.*;
import deus.btd.pipeline.io.AtomProjectLoader;
import deus.btd.pipeline.io.AtomTomlLoader;
import deus.btd.pipeline.io.ZipResources;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static deus.btd.utils.ConfigManager.itemGoc;
import static net.minecraft.core.entity.EntityDispatcher.stringIdToClassMap;

public class ProjectProcessor {

	public static ProjectProcessed process(ProjectResources project) {
		Main.LOGGER.info("Processing projects...");

		stringIdToClassMap.forEach((k, v) -> {
			System.out.println("K: " + k);
		});


		Map<AtomType, List<?>> atoms = AtomProjectLoader.loadAtomsFromProject(project);

		Main.LOGGER.info("- Blocks");
		Map<String, Block<?>> blocks =
			BlockFactory.build((List<CompiledBlock>) atoms.get(AtomType.BLOCK));

		Main.LOGGER.info("- Items");
		Map<String, Item> items =
			ItemFactory.build((List<CompiledItem>) atoms.get(AtomType.ITEM));

		Main.LOGGER.info("- Biomes");
		Map<String, AtomBiome> biomes =
			BiomeFactory.build((List<CompiledBiome>) atoms.get(AtomType.BIOME));

		Main.LOGGER.info("- Worlds");
		WorldFactory.build((List<CompiledWorld>) atoms.get(AtomType.WORLD));

		return new ProjectProcessed(
			project.name(),
			project,
			blocks,
			items,
			biomes,
			atoms
		);
	}

	public static List<ProjectProcessed> processProjects(List<ProjectResources> projectResources) {
		return projectResources.stream()
			.map(ProjectProcessor::process)
			.collect(Collectors.toList());
	}
}
