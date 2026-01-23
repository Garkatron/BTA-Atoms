package deus.btd.toml.project;

import deus.btd.mixin.AtlasStitcherAccessor;
import deus.btd.mixin.TexturePackCustomAccessor;
import deus.btd.toml.AtomCompiler;
import deus.btd.toml.AtomLoader;
import deus.btd.toml.types.AtomType;
import deus.btd.toml.types.CompiledAtomProjectHeader;
import deus.btd.toml.types.CompiledBlock;
import deus.btd.toml.types.CompiledItem;
import deus.btd.utils.ZipResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.core.util.collection.NamespaceID;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static deus.btd.Main.LOGGER;
import static deus.btd.Main.MOD_ID;

public class ProjectResources implements Closeable {

	private final ZipResources zipResources;
	protected boolean assets;
	protected boolean data;
	protected boolean recipes;
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
		recipes = zipResources.existsFolder("/recipes");
		data = zipResources.existsFolder("/data");
		emptyData = data && zipResources.isEmpty("/data");
		cache = new ProjectDataCache();

		header = AtomLoader.loadAtomProjectHeader(zipResources.get("manifest.atom"));
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
						AtomLoader.processAtomFile(name, atomFile, compiledAtoms);
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


	// Don't ask about it...
	public void loadTextures() {
		if (!assets) return;

		try {
			File tempZip = File.createTempFile(this.getName(), ".zip");
			tempZip.deleteOnExit();

			try (ZipFile originalZip = new ZipFile(originalZipPath.toFile());
				 ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip))) {

				Enumeration<? extends ZipEntry> entries = originalZip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();

					if (entry.getName().startsWith("assets/")) {
						ZipEntry newEntry = new ZipEntry(entry.getName());
						zos.putNextEntry(newEntry);

						try (InputStream is = originalZip.getInputStream(entry)) {
							byte[] buffer = new byte[4096];
							int len;
							while ((len = is.read(buffer)) > 0) {
								zos.write(buffer, 0, len);
							}
						}

						zos.closeEntry();
					}
				}
			}

			texturesPack = new TexturePackCustom(tempZip);

			if (this.zipResources.exists("assets/pack.png")) {
				((TexturePackCustomAccessor)texturesPack).setThumbnailBuffer(this.zipResources.readImageSafe("assets/pack.png"));
			}

			texturesPack.readZipFile();
			texturesPack.readTexturePackManifest();
			LOGGER.info("Loaded texturepack from atompack successfully");

			Minecraft.getMinecraft().texturePackList.selectedPacks.add(texturesPack);

		} catch (IOException e) {
			LOGGER.error("Failed to load texture pack", e);
		}
	}


	public void loadDatapack() {
		if (recipes) {
			AtomLoader.loadDatapack(getName(), this.zipResources);

		}

	}

}
