package deus.btd.pipeline.project;

import deus.btd.Main;
import deus.btd.pipeline.build.BlockFactory;
import deus.btd.pipeline.build.ItemFactory;
import deus.btd.pipeline.compile.types.AtomType;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
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

public class ProjectProcessor {

	public static ProjectProcessed process(ProjectResources project) {

		Map<AtomType, List<?>> atoms = AtomProjectLoader.loadAtomsFromProject(project);

		Map<String, Block<?>> blocks =
			BlockFactory.build((List<CompiledBlock>) atoms.get(AtomType.BLOCK));

		Map<String, Item> items =
			ItemFactory.build((List<CompiledItem>) atoms.get(AtomType.ITEM));

		return new ProjectProcessed(
			project.name(),
			project,
			blocks,
			items,
			atoms
		);
	}

	public static List<ProjectProcessed> processProjects(List<ProjectResources> projectResources) {
		return projectResources.stream()
			.map(ProjectProcessor::process)
			.collect(Collectors.toList());
	}
}
