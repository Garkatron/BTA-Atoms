package deus.atoms.utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class ZipResources implements Closeable {

	protected final FileSystem fs;

	public ZipResources(FileSystem fs) {
		this.fs = fs;
	}

	public Path get(String path) {
		return fs.getPath(path);
	}

	public boolean exists(String path) {
		return Files.exists(get(path));
	}

	public Stream<Path> list(String path) throws IOException {
		return Files.list(get(path));
	}

	public boolean existsFolder(String path) throws IOException {
		Path p = get(path);
		if (!Files.exists(p)) return false;
		return Files.isDirectory(p);
	}


	@Override
	public void close() throws IOException {
		fs.close();
	}

	public boolean isEmpty(String path) throws IOException {
		try (Stream<Path> s = list(path)) {
			return !s.findAny().isPresent();
		}
	}

}

