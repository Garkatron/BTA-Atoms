package deus.atoms.toml;

import deus.atoms.mixin.AtlasStitcherAccessor;
import deus.atoms.toml.project.ProjectProcessed;
import deus.atoms.toml.project.ProjectResources;
import deus.atoms.toml.types.*;
import deus.atoms.utils.ConfigManager;
import deus.atoms.utils.ImageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.collection.NamespaceID;
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
	public static final Path ATOMS_TEXTURES_PATH = Paths.get(ATOMS_PATH.toString(), "assets", "textures");

	public static void createFolders() throws IOException {
		Files.createDirectories(ATOMS_FILES_PATH);
		Files.createDirectories(ATOMS_TEXTURES_PATH);
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

	public static void loadTextures(Map<AtomType, List<?>> atoms) {
		loadBlockTextures((List<CompiledBlock>) atoms.get(AtomType.BLOCK));
		loadItemTextures((List<CompiledItem>) atoms.get(AtomType.ITEM));

	}

	public static void loadItemTextures(List<CompiledItem> compiledItems) {
		LOGGER.info("Registering textures for {} item atoms...", compiledItems.size());

		for (CompiledItem atom : compiledItems) {
			// Validaciones básicas
			if (!validateAtom(atom, "item")) continue;
			if (atom.textures.texture == null) {
				LOGGER.warn("Texture in atom '{}' is null. Skipping.", atom.data.name);
				continue;
			}

			boolean isBase64 = "base64".equals(atom.textures.encoding);

			// Procesar textura
			String texPath;
			if (isBase64) {
				LOGGER.info("Processing base64 texture for item atom '{}'.", atom.data.name);
				texPath = ImageUtils.loadB64PNG(atom.textures.texture, "item", atom.data.name);
			} else {
				LOGGER.info("Using path texture for item atom '{}'.", atom.data.name);
				texPath = atom.textures.texture;
			}

			// Registrar textura
			registerTexture(texPath, atom.data.name, true);
		}

		LOGGER.info("Item texture registration complete.");
	}

	public static void loadBlockTextures(List<CompiledBlock> compiledBlocks) {
		LOGGER.info("Registering textures for {} block atoms...", compiledBlocks.size());

		String[] faces = {"top", "bottom", "north", "south", "west", "east"};

		for (CompiledBlock atom : compiledBlocks) {
			// Validaciones básicas
			if (!validateAtom(atom, "block")) continue;
			if (atom.textures.faces == null) {
				LOGGER.warn("Atom '{}' has no faces defined. Skipping.", atom.data.name);
				continue;
			}

			boolean isBase64 = "base64".equals(atom.textures.encoding);

			// Procesar cada cara
			for (String face : faces) {
				String tex = getFaceTexture(atom.textures.faces, face);

				if (tex == null) {
					LOGGER.debug("Texture for face '{}' in atom '{}' is null. Skipping.", face, atom.data.name);
					continue;
				}

				// Procesar textura
				String texPath;
				if (isBase64) {
					LOGGER.info("Processing base64 texture for face '{}' of block atom '{}'.", face, atom.data.name);
					texPath = ImageUtils.loadBlockB64PNG(tex, atom.data.name, face);
					TEXTURE_PATHS.put(atom.data.name + "_" + face, texPath);
				} else {
					LOGGER.info("Using path texture for face '{}' of block atom '{}'.", face, atom.data.name);
					texPath = tex;
				}

				// Registrar textura
				registerTexture(texPath, atom.data.name, false);
			}
		}

		LOGGER.info("Block texture registration complete.");
	}

	/**
	 * Valida que un átomo tenga los datos necesarios
	 */
	private static boolean validateAtom(Object atom, String type) {
		String name = null;
		int formatVersion = 0;
		Object textures = null;

		if (atom instanceof CompiledItem) {
			CompiledItem item = (CompiledItem) atom;
			name = item.data.name;
			formatVersion = item.meta.formatVersion;
			textures = item.textures;
		} else if (atom instanceof CompiledBlock) {
			CompiledBlock block = (CompiledBlock) atom;
			name = block.data.name;
			formatVersion = block.meta.formatVersion;
			textures = block.textures;
		}

		if (formatVersion != AtomCompiler.AtomFormatVersion) {
			LOGGER.warn("Wrong format version for {} atom. Skipping.", type);
			return false;
		}

		if (name == null) {
			LOGGER.warn("Encountered {} atom without a name. Skipping.", type);
			return false;
		}

		if (textures == null) {
			LOGGER.warn("{} atom '{}' has no textures table. Skipping.",
				type.substring(0, 1).toUpperCase() + type.substring(1), name);
			return false;
		}

		return true;
	}

	/**
	 * Obtiene la textura de una cara específica
	 */
	private static String getFaceTexture(CompiledBlock.Textures.Faces faces, String face) {
		switch (face) {
			case "top":    return faces.top;
			case "bottom": return faces.bottom;
			case "north":  return faces.north;
			case "south":  return faces.south;
			case "west":   return faces.west;
			case "east":   return faces.east;
			default:       return null;
		}
	}

	/**
	 * Registra una textura en el atlas correspondiente
	 */
	private static void registerTexture(String texPath, String atomName, boolean isItem) {
		NamespaceID id = NamespaceID.getPermanent(MOD_ID, texPath);
		LOGGER.info("Registering texture '{}' for atom '{}'.", id, atomName);

		if (isItem) {
			((AtlasStitcherAccessor) TextureRegistry.itemAtlas).callGetTexture(id);
		} else {
			((AtlasStitcherAccessor) TextureRegistry.blockAtlas).callGetTexture(id);
		}
	}

	public static void loadAtoms(Map<AtomType, List<?>> atoms) {
		loadAtomBlocks((List<CompiledBlock>) atoms.get(AtomType.BLOCK));
		loadAtomItems((List<CompiledItem>) atoms.get(AtomType.ITEM));
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
				.filter(p -> p.getFileName().toString().endsWith(".project.atom"))
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
