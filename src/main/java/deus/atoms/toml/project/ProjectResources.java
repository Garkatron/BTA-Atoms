package deus.atoms.toml.project;

import deus.atoms.toml.AtomLoader;
import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledAtomProjectHeader;
import deus.atoms.utils.ZipResources;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static deus.atoms.Main.LOGGER;

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
	public Map<String, String> ITEM_TEXTURES = null;
	public Map<String, String> BLOCK_TEXTURES = null;

	public ProjectResources(Path projectPath) throws Exception {
		this.zipResources = new ZipResources(FileSystems.newFileSystem(projectPath, (ClassLoader) null)) {};

		assets = zipResources.existsFolder("/assets");
		textures = zipResources.existsFolder("/assets/textures");
		assetsBlock = zipResources.existsFolder("/assets/textures/block");
		assetsItem = zipResources.existsFolder("/assets/textures/item");
		data = zipResources.existsFolder("/data");
		emptyData = data && zipResources.isEmpty("/data");
		cache = new ProjectDataCache();

		if (assets && textures && assetsItem) {
			ITEM_TEXTURES = getTextures(zipResources.get("/assets/textures/item"));
		}

		if (assets && textures && assetsBlock) {
			BLOCK_TEXTURES = getTextures(zipResources.get("/assets/textures/block"));
		}

		header = AtomLoader.loadAtomProjectHeader(zipResources.get(".project.atom"));
		name = header.data.name;

		cache.ATOMS = loadAtoms();
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
}
