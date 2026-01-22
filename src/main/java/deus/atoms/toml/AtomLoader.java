package deus.atoms.toml;

import deus.atoms.toml.project.ProjectProcessed;
import deus.atoms.toml.project.ProjectResources;
import deus.atoms.toml.types.*;
import deus.atoms.utils.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static deus.atoms.Main.*;
import static deus.atoms.utils.ConfigManager.configBlockIDsFromNames;
import static deus.atoms.utils.ConfigManager.configItemsIDsFromNames;

public class AtomLoader {

	public static final Map<String, String> TEXTURE_PATHS = new HashMap<>();

	public static final Path ATOMS_PATH = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "atoms");
	public static final Path ATOMS_FILES_PATH = Paths.get(ATOMS_PATH.toString(), "data");

	public static void createFolders() throws IOException {
		Files.createDirectories(ATOMS_FILES_PATH);
	}

	public static Map<AtomType, List<?>> loadAllAtoms() throws IOException {
		Map<AtomType, List<?>> compiledAtoms = new EnumMap<>(AtomType.class);
		compiledAtoms.put(AtomType.BLOCK, new ArrayList<CompiledBlock>());
		compiledAtoms.put(AtomType.ITEM, new ArrayList<CompiledItem>());
		compiledAtoms.put(AtomType.PROJECT, new ArrayList<CompiledItem>());

		if (!Files.exists(ATOMS_FILES_PATH) || !Files.isDirectory(ATOMS_FILES_PATH)) {
			LOGGER.warn("0 Atoms found at {}.", ATOMS_FILES_PATH);
			return Collections.emptyMap();
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(ATOMS_FILES_PATH)) {
			if (!stream.iterator().hasNext()) {
				LOGGER.warn("0 Atoms found at {}.", ATOMS_FILES_PATH);
				return Collections.emptyMap();
			}
		}

		try (Stream<Path> paths = Files.walk(ATOMS_FILES_PATH)) {
			paths
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().endsWith(".atom"))
				.forEach(tomlFile -> {
					try {
						processAtomFile(tomlFile, compiledAtoms);
					} catch (Exception e) {
						LOGGER.error("Failed to load atom file: {}", tomlFile, e);
					}
				});
		}

		LOGGER.info("Loaded {} blocks, {} items, {} tools",
			compiledAtoms.get(AtomType.BLOCK).size(),
			compiledAtoms.get(AtomType.ITEM).size());

		return compiledAtoms;
	}

	@SuppressWarnings("unchecked")
	public static void processAtomFile(Path tomlFile, Map<AtomType, List<?>> compiledAtoms) throws Exception {
		String fileName = tomlFile.getFileName().toString();
		AtomType type = AtomType.fromFileName(fileName);
		TomlParseResult tomlResult = loadToml(tomlFile);

		switch (type) {
			case BLOCK:
				((List<CompiledBlock>) compiledAtoms.get(AtomType.BLOCK))
					.add(AtomTomlDeserializer.fromToml(tomlResult, CompiledBlock.class));
				break;
			case ITEM:
				((List<CompiledItem>) compiledAtoms.get(AtomType.ITEM))
					.add(AtomTomlDeserializer.fromToml(tomlResult, CompiledItem.class));
				break;
		}
	}

	public static CompiledAtomProjectHeader loadAtomProjectHeader(Path tomlFile) throws Exception {
		return AtomTomlDeserializer.fromToml(loadToml(tomlFile), CompiledAtomProjectHeader.class);
	}


	public static TomlParseResult loadToml(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		StringBuilder content = new StringBuilder();
		for (String line : lines) {
			content.append(line).append("\n");
		}

		TomlParseResult result = Toml.parse(content.toString());
		return result;
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
					return author + "_" + name;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledBlock atom : compiledBlocks) {
				blocks.add(AtomCompiler.convertoIntoBlocks(atom));
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
					return author + "_" + name;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configItemsIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledItem atom : compiledItems) {
				items.add(AtomCompiler.convertIntoItem(atom));
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
		return items;
	}

	public static List<ProjectResources> loadProjects(Path folder) {
		List<ProjectResources> projectResources = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(folder)) {
			paths
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().endsWith(".project.atom.zip"))
				.forEach(zip -> {
					try {
						projectResources.add(new ProjectResources(zip));
					} catch (Exception e) {
						LOGGER.error("Failed to load atom file: {}", zip, e);
					}
				});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return projectResources;
	}

	public static List<ProjectProcessed> processProjects(List<ProjectResources> projectResources) {
		List<ProjectProcessed> projects = new ArrayList<>();
		projectResources.forEach((pj -> {
			List<CompiledItem> items = (List<CompiledItem>) pj.getAtoms().get(AtomType.ITEM);
			List<CompiledBlock> blocks = (List<CompiledBlock>) pj.getAtoms().get(AtomType.BLOCK);

			ProjectProcessed processed = new ProjectProcessed(
				pj.getName(),
				pj,
				AtomLoader.loadAtomBlocks(blocks).stream()

					.collect(Collectors.toMap(block -> ((Block<?>)block).namespaceId().toString(), Function.identity())),
				AtomLoader.loadAtomItems(items).stream()

					.collect(Collectors.toMap(item -> ((Item)item).namespaceID.toString(), Function.identity()))
			);

			projects.add(processed);
		}));
		return projects;
	}

}
