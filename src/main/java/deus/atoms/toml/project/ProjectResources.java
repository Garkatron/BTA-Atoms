package deus.atoms.toml.project;

import deus.atoms.mixin.AtlasStitcherAccessor;
import deus.atoms.toml.AtomCompiler;
import deus.atoms.toml.AtomLoader;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledAtomProjectHeader;
import deus.atoms.toml.types.CompiledBlock;
import deus.atoms.toml.types.CompiledItem;
import deus.atoms.utils.ZipResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.core.util.collection.NamespaceID;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static deus.atoms.Main.LOGGER;
import static deus.atoms.Main.MOD_ID;

public class ProjectResources implements Closeable {

	private final ZipResources zipResources;
	protected boolean assets;
	protected boolean textures;
	protected boolean assetsBlock;
	protected boolean assetsItem;
	protected boolean data;
	protected boolean emptyData;
	protected ProjectDataCache cache;
	protected String name;
	protected CompiledAtomProjectHeader header;
	public TexturePackCustom texturesPack;
	private final Path originalZipPath;

	public ProjectResources(Path projectPath) throws Exception {
		this.originalZipPath = projectPath;
		this.zipResources = new ZipResources(FileSystems.newFileSystem(projectPath, (ClassLoader) null)) {};
		assets = zipResources.existsFolder("/assets");
		data = zipResources.existsFolder("/data");
		emptyData = data && zipResources.isEmpty("/data");
		cache = new ProjectDataCache();




		header = AtomLoader.loadAtomProjectHeader(zipResources.get(".project.atom"));
		name = header.data.name;

		cache.ATOMS = loadAtoms();
		// loadTextures(cache.ATOMS);
	}

	public String getName() {
		return name;
	}

	protected Map<String, String> getTextures(Path fpath) {
		Map<String, String> texturesMap = new HashMap<>();

		try (Stream<Path> paths = Files.walk(fpath)) {
			paths
				.filter(Files::isRegularFile)
				.forEach(path -> {
					try {
						byte[] bytes = Files.readAllBytes(path);
						String encoded = Base64.getUrlEncoder().encodeToString(bytes);
						texturesMap.put(path.toString(), encoded);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
		} catch (IOException | UncheckedIOException e) {
			return null;
		}

		return texturesMap;
	}

	public Map<AtomType, List<?>> getAtoms() {
		return cache.ATOMS;
	}

	protected Map<AtomType, List<?>> loadAtoms() {
		Map<AtomType, List<?>> compiledAtoms = new HashMap<>();
		if (!data || emptyData) return compiledAtoms;

		for (AtomType type : AtomType.values()) {
			compiledAtoms.put(type, new ArrayList<>());
		}

		try (Stream<Path> paths = Files.walk(zipResources.get("/data"))) {
			paths
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().endsWith(".atom"))
				.forEach(atomFile -> {
					try {
						AtomLoader.processAtomFile(atomFile, compiledAtoms);
					} catch (Exception e) {
						LOGGER.error("Failed to load atom file: {}", atomFile, e);
					}
				});
		} catch (IOException | UncheckedIOException e) {
			LOGGER.error("Failed to walk data folder", e);
		}

		return compiledAtoms;
	}

	@Override
	public void close() throws IOException {
		zipResources.close();
	}

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

	public void loadTextures() {
		if (assets) {
			File datapackZip = originalZipPath.toFile();
			texturesPack = new TexturePackCustom(datapackZip);
			texturesPack.readZipFile();

			try {
				texturesPack.readTexturePackManifest();
				LOGGER.info("Loaded texturepack from atompack succefully");
			} catch (IOException e) {
				LOGGER.error("Failed to read texture pack manifest", e);
			}

			Minecraft.getMinecraft().texturePackList.selectedPacks.add(texturesPack);
		}

	}

}
