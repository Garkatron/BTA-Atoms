package deus.btd.pipeline.io;

import deus.btd.pipeline.compile.types.*;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;



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




}
