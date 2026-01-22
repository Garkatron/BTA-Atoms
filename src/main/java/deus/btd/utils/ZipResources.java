package deus.btd.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
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

	public Path extractTemp(String zipPath) throws IOException {
		Path tempFile = Files.createTempFile("zip_texture_", ".png");
		try (InputStream is = getInputStreamFromZip(zipPath)) {
			Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		}
		return tempFile;
	}

	public InputStream getInputStreamFromZip(String zipPath) throws IOException {
		Path p = get(zipPath);
		if (!Files.exists(p)) {
			throw new IOException("File not found in ZIP: " + zipPath);
		}
		return Files.newInputStream(p);
	}

	public BufferedImage readImage(String zipPath) throws IOException {
		try (InputStream is = getInputStreamFromZip(zipPath)) {
			return ImageIO.read(is);
		}
	}


	public BufferedImage readImageSafe(String zipPath) {
		try {
			return readImage(zipPath);
		} catch (IOException e) {
			return null;
		}
	}

}

