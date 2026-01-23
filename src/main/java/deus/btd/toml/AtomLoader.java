package deus.btd.toml;

import com.b100.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import deus.btd.toml.project.ProjectProcessed;
import deus.btd.toml.project.ProjectResources;
import deus.btd.toml.types.*;
import deus.btd.utils.ConfigManager;
import deus.btd.utils.ZipResources;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.Item;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static deus.btd.Main.*;
import static deus.btd.utils.ConfigManager.configBlockIDsFromNames;
import static deus.btd.utils.ConfigManager.configItemsIDsFromNames;

public class AtomLoader {


	public static final Path ATOMS_PATH = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "btd");
	public static final Path ATOMS_FILES_PATH = Paths.get(ATOMS_PATH.toString(), "data");
	public static final Path CONFIG_FILE_PATH = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "config/better_than_datapacks.cfg");

	public static void createFolders() throws IOException {
		Files.createDirectories(ATOMS_FILES_PATH);
	}

	public static void deletePreviousConfig() {
		if (CONFIG_FILE_PATH.toFile().exists()) {
			LOGGER.info("Deleting previous config.");
			boolean result = AtomLoader.CONFIG_FILE_PATH.toFile().delete();
			LOGGER.info("Deleted: {}", result);
		}
	}

	@SuppressWarnings("unchecked")
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
					return formatAtomKey(author, name);
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());


			configBlockIDsFromNames(keys, ConfigManager.TOML);
			ConfigManager.makeConfig();

			// CREATING INSTANCES
			LOGGER.info("Creating block instances for atoms...");


			for (CompiledBlock atom : compiledBlocks) {
				if (AtomCompiler.isValidBlockAtom(atom)) continue;
				blocks.add(AtomCompiler.convertIntoBlocks(atom));
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
		return blocks;
	}

	public static String formatAtomKey(String author, String atomName) {
		return (author + "_" + atomName).trim().replace(" ", "_");
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
				if (AtomCompiler.isValidItemAtom(atom)) continue;
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
				.filter(p -> p.getFileName().toString().endsWith(AtomType.PROJECT.suffix()))
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

	public static void loadDatapack(String name, ZipResources resources) {
		LOGGER.info("Loading data from {}", name);

		Path manifestPath = resources.get("recipes/manifest.json");
		if (!Files.exists(manifestPath)) {
			LOGGER.warn("manifest.json not found in {}", name);
			return;
		}

		try (InputStream is = Files.newInputStream(manifestPath);
			 InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {

			Gson gson = new Gson();
			TypeToken<Map<String, List<String>>> token = new TypeToken<Map<String, List<String>>>() {};
			Map<String, List<String>> map = gson.fromJson(reader, token);

			List<String> recipeFiles = map.get("added_recipes");
			List<String> groupFiles = map.get("added_item_groups");
			List<String> removedRecipes = map.get("removed_recipes");

			// --- Remove recipes ---
			if (removedRecipes != null && !removedRecipes.isEmpty()) {
				for (String removedRecipe : removedRecipes) {
					String[] deconstructedKey = Registries.RECIPES.deconstructKey(removedRecipe);
					Registries.RECIPES.getGroupFromKey(removedRecipe).unregister(deconstructedKey[2]);
				}
				LOGGER.info("Removed {} recipes", removedRecipes.size());
			}

			// --- Load item groups ---
			if (groupFiles != null && !groupFiles.isEmpty()) {
				for (String groupFile : groupFiles) {
					Path groupFilePath = resources.get("recipes/" + groupFile);
					if (Files.exists(groupFilePath)) {
						try (InputStream groupIs = Files.newInputStream(groupFilePath)) {
							String contents = StringUtils.readInputString(groupIs);
							LOGGER.info("Loading item groups from {}/{}", name, groupFile);
							DataLoader.loadItemGroupsFromString(contents);
						}
					}
				}
			}

			// --- Load recipes ---
			if (recipeFiles != null && !recipeFiles.isEmpty()) {
				for (String recipeFile : recipeFiles) {
					Path recipePath = resources.get("recipes/ " + recipeFile);
					if (Files.exists(recipePath)) {
						try (InputStream recipeIs = Files.newInputStream(recipePath)) {
							String contents = StringUtils.readInputString(recipeIs);
							LOGGER.info("Loading recipes from {}/{}", name, recipeFile);
							DataLoader.loadRecipesFromString(contents);
						}
					}
				}
			}

		} catch (IOException e) {
			LOGGER.error("Failed to load data from {}", name, e);
		}
	}


}
