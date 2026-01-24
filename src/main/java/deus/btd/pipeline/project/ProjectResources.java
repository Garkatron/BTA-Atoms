package deus.btd.pipeline.project;

import deus.btd.pipeline.compile.types.CompiledAtomProjectHeader;
import deus.btd.pipeline.io.AtomTomlLoader;
import deus.btd.pipeline.io.ZipResources;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ProjectResources implements Closeable {

	private final ZipResources zip;
	public final Path originalZipPath;
	private final CompiledAtomProjectHeader header;

	public ProjectResources(Path projectPath) throws Exception {
		this.zip = new ZipResources(FileSystems.newFileSystem(projectPath, (ClassLoader) null)) {
		};
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

	public boolean hasAssets() {
		return this.zip.exists("/assets");
	}

	public boolean hasRecipes() {
		return this.zip.exists("/recipes");
	}

	public boolean hasdata() {
		return this.zip.exists("/data");
	}
}
