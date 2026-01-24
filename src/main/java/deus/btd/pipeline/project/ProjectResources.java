package deus.btd.pipeline.project;

import deus.btd.mixin.TexturePackCustomAccessor;
import deus.btd.pipeline.compile.types.AtomType;
import deus.btd.pipeline.compile.types.CompiledAtomProjectHeader;
import deus.btd.pipeline.io.AtomTomlLoader;
import deus.btd.pipeline.io.ZipResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePackCustom;

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

public class ProjectResources implements Closeable {

	private final ZipResources zip;
	private final Path originalZipPath;
	private final CompiledAtomProjectHeader header;

	public ProjectResources(Path projectPath) throws Exception {
		this.zip = new ZipResources(FileSystems.newFileSystem(projectPath, (ClassLoader) null)) {};
		this.originalZipPath = projectPath;
		this.header = AtomTomlLoader.loadProjectHeader(zip.get("manifest.atom"));
	}

	public String name() {
		return header.data.name;
	}

	public ZipResources zip() {
		return zip;
	}

	@Override
	public void close() throws IOException {
		zip.close();
	}
}
