package deus.atoms.toml;

import deus.atoms.mixin.AtlasStitcherAccessor;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import deus.atoms.utils.ConfigManager;
import deus.atoms.utils.ImageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static deus.atoms.Main.*;
import static deus.atoms.utils.ConfigManager.configBlockIDsFromNames;
import static deus.atoms.utils.ConfigManager.configItemsIDsFromNames;

public class AtomLoader {

	public static final Map<String, String> TEXTURE_PATHS = new HashMap<>();

	public static final Path ATOMS_PATH = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "atoms");
	public static final Path ATOMS_FILES_PATH = Paths.get(ATOMS_PATH.toString(), "blocks");
	public static final Path ATOMS_TEXTURES_PATH = Paths.get(ATOMS_PATH.toString(), "assets", "textures");

	public static void createFolders() throws IOException {
		Files.createDirectories(ATOMS_FILES_PATH);
		Files.createDirectories(ATOMS_TEXTURES_PATH);
	}

	public static Map<AtomType, List<?>> loadAllAtoms() throws IOException {
		Map<AtomType, List<?>> compiledAtoms = new EnumMap<>(AtomType.class);
		compiledAtoms.put(AtomType.BLOCK, new ArrayList<CompiledBlock>());
		compiledAtoms.put(AtomType.ITEM, new ArrayList<CompiledItem>());

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
	private static void processAtomFile(Path tomlFile, Map<AtomType, List<?>> compiledAtoms) throws Exception {
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
		loadItemTexture((List<CompiledItem>) atoms.get(AtomType.ITEM));

	}

	public static void loadItemTexture(List<CompiledItem> compiledItems) {
		// PROCESSING ALL
		LOGGER.info("Registering textures for {} atoms...", compiledItems.size());

		for (CompiledItem atom : compiledItems) {

			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) continue;

			// IF NOT NAME AVOID IT
			if (atom.data.name == null) {
				LOGGER.warn("Encountered atom without a name. Skipping texture registration.");
				continue;
			}

			// IF NOT TEXTURES AVOID IT
			if (atom.textures == null) {
				LOGGER.warn("Atom '{}' has no textures table. Skipping.", atom.data.name);
				continue;
			}

			boolean isBase64 = atom.textures.encoding.equals("base64");


			if (atom.textures.texture == null) {
				LOGGER.warn("Texture in atom '{}' is null. Skipping.", atom.data.name);
				continue;
			}

			// CREATING TEXTURE INTO ATOMS TEXTURES FOLDER
			String texPath;
			if (isBase64) {
				LOGGER.info("Texture '{}' for atom '{}' is base64.", atom.data.name, atom.data.name);
				String relativePath = ImageUtils.loadB64PNG(atom.textures.texture, "item", atom.data.name);
				texPath = relativePath;
			} else {
				LOGGER.info("Texture for atom '{}' is a path.", atom.data.name);
				texPath = atom.data.name;
			}

			// REGISTER THE TEXTURE
			NamespaceID id = NamespaceID.getPermanent(MOD_ID, MOD_ID + ":" +  atom.data.name);
			LOGGER.info("Registering texture '{}' for atom '{}'.", id, atom.data.name);

			((AtlasStitcherAccessor) TextureRegistry.itemAtlas).callGetTexture(id);

		}

		// READING BLOCK FIELDS
		LOGGER.info("Assigning block IDs from config...");
	}

	public static void loadBlockTextures(List<CompiledBlock> compiledBlocks) {
		// PROCESSING ALL
		LOGGER.info("Registering textures for {} atoms...", compiledBlocks.size());

		for (CompiledBlock atom : compiledBlocks) {

			if (atom.meta.formatVersion != AtomCompiler.AtomFormatVersion) continue;

			// IF NOT NAME AVOID IT
			if (atom.data.name == null) {
				LOGGER.warn("Encountered atom without a name. Skipping texture registration.");
				continue;
			}

			// IF NOT TEXTURES AVOID IT
			if (atom.textures == null) {
				LOGGER.warn("Atom '{}' has no textures table. Skipping.", atom.data.name);
				continue;
			}

			boolean isBase64 = atom.textures.encoding.equals("base64");

			// Iterate over all entries in the table
			String[] faces = new String[] {
				"top", "bottom", "north", "south", "west", "east"
			};

			for (String face : faces) {
				String tex = null;

				switch (face) {
					case "top":    tex = atom.textures.faces.top; break;
					case "bottom": tex = atom.textures.faces.bottom; break;
					case "north":  tex = atom.textures.faces.north; break;
					case "south":  tex = atom.textures.faces.south; break;
					case "west":   tex = atom.textures.faces.west; break;
					case "east":   tex = atom.textures.faces.east; break;
				}

				if (face == null) {
					LOGGER.warn("Texture for face '{}' in atom '{}' is null. Skipping.", face, atom.data.name);
					continue;
				}

				// CREATING TEXTURE INTO ATOMS TEXTURES FOLDER
				String texPath;
				if (isBase64) {
					LOGGER.info("Texture '{}' for atom '{}' is base64.", face, atom.data.name);
					String relativePath = ImageUtils.loadBlockB64PNG(tex, atom.data.name, face);
					texPath = relativePath;
				} else {
					LOGGER.info("Texture '{}' for atom '{}' is a path.", face, atom.data.name);
					texPath = face;
				}

				// REGISTER THE TEXTURE
				NamespaceID id = NamespaceID.getPermanent(MOD_ID, texPath);
				LOGGER.info("Registering texture '{}' for atom '{}'.", id, atom.data.name);

				((AtlasStitcherAccessor) TextureRegistry.blockAtlas).callGetTexture(id);
			}

		}

		// READING BLOCK FIELDS
		LOGGER.info("Assigning block IDs from config...");
	}

	public static void loadAtoms(Map<AtomType, List<?>> atoms) {
		loadAtomBlocks((List<CompiledBlock>) atoms.get(AtomType.BLOCK));
		loadAtomItems((List<CompiledItem>) atoms.get(AtomType.ITEM));
	}

	public static void loadAtomBlocks(List<CompiledBlock> compiledBlocks) {
		System.out.println(compiledBlocks.get(0).data);


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
				AtomCompiler.convertoIntoBlocks(atom);
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
	}

	public static void loadAtomItems(List<CompiledItem> compiledItems) {
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
				AtomCompiler.convertIntoItem(atom);
			}

			LOGGER.info("Atom blocks successfully created: {}", blocks.size());

		} catch (Exception e) {
			LOGGER.error("Failed to initialize atoms.", e);
			throw new RuntimeException(e);
		}
	}



}
