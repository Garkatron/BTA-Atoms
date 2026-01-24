package deus.btd.pipeline.io;

import deus.btd.pipeline.build.BlockFactory;
import deus.btd.pipeline.build.ItemFactory;
import deus.btd.pipeline.compile.BlockCompiler;
import deus.btd.pipeline.compile.ItemCompiler;
import deus.btd.pipeline.compile.types.*;
import deus.btd.utils.ConfigManager;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static deus.btd.Main.LOGGER;
import static deus.btd.Main.blocks;
import static deus.btd.pipeline.io.AtomTomlUtils.formatAtomKey;
import static deus.btd.utils.ConfigManager.configBlockIDsFromNames;
import static deus.btd.utils.ConfigManager.configItemsIDsFromNames;

public class AtomTomlLoader {
	public static TomlParseResult loadToml(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		StringBuilder content = new StringBuilder();
		for (String line : lines) {
			content.append(line).append("\n");
		}

		TomlParseResult result = Toml.parse(content.toString());
		return result;
	}

	public static void processAtomFile(String name, Path tomlFile, Map<AtomType, List<?>> compiledAtoms) throws Exception {
		String fileName = tomlFile.getFileName().toString();
		AtomType type = AtomType.fromFileName(fileName);
		TomlParseResult tomlResult = loadToml(tomlFile);

		switch (type) {
			case BLOCK: {
				CompiledBlock block = AtomTomlDeserializer.fromToml(tomlResult, CompiledBlock.class);
				block.namespace = name;
				((List<CompiledBlock>) compiledAtoms.get(AtomType.BLOCK))
					.add(block);
				break;
			}
			case ITEM: {
				CompiledItem item = AtomTomlDeserializer.fromToml(tomlResult, CompiledItem.class);
				item.namespace = name;
				((List<CompiledItem>) compiledAtoms.get(AtomType.ITEM))
					.add(item);
				break;
			}
			case BIOME: {
				CompiledBiome biome = AtomTomlDeserializer.fromToml(tomlResult, CompiledBiome.class);
				biome.namespace = name;
				((List<CompiledBiome>) compiledAtoms.get(AtomType.BIOME))
					.add(biome);
				break;
			}
			case WEATHER: {
				CompiledWeather biome = AtomTomlDeserializer.fromToml(tomlResult, CompiledWeather.class);
				biome.namespace = name;
				((List<CompiledWeather>) compiledAtoms.get(AtomType.WEATHER))
					.add(biome);
				break;
			}
		}
	}

	public static CompiledAtomProjectHeader loadProjectHeader(Path tomlFile) throws Exception {
		return AtomTomlDeserializer.fromToml(loadToml(tomlFile), CompiledAtomProjectHeader.class);
	}


	public static List<Block<?>> loadAtomBlocks(List<CompiledBlock> compiledBlocks) {
		System.out.println(compiledBlocks.get(0).data);
		List<Block<?>> blocks = new ArrayList<>();

		try {

			// CREATE ATOMS MOD CONFIG WITH ALL LOADED BLOCKS NAMES
			List<String> keys = compiledBlocks.stream()
				.map(atom -> {
					String author = atom.meta != null && atom.meta.author != null ? atom.meta.author : "Unnamed";
					String name = atom.data != null && atom.data.name != null ? atom.data.name : "UnnamedBlock";
					return formatAtomKey(author, name);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledBlock atom : compiledBlocks) {
				if (!BlockCompiler.validate(atom)) continue;
				blocks.add(BlockFactory.build(atom));
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
		return blocks;
	}




	public static List<Item> loadAtomItems(List<CompiledItem> compiledItems) {
		List<Item> items = new ArrayList<>();

		try {

			// CREATE ATOMS MOD CONFIG WITH ALL LOADED BLOCKS NAMES
			List<String> keys = compiledItems.stream()
				.map(atom -> {
					String author = atom.meta != null && atom.meta.author != null ? atom.meta.author : "Unnamed";
					String name = atom.data != null && atom.data.name != null ? atom.data.name : "UnnamedItem";
					return formatAtomKey(author, name);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configItemsIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledItem atom : compiledItems) {
				if (!ItemCompiler.validate(atom)) continue;
				items.add(ItemFactory.build(atom));
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
		return items;
	}
}
