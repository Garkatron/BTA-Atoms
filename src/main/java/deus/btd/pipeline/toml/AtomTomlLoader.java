package deus.btd.pipeline.toml;

import deus.btd.pipeline.io.AtomTomlDeserializer;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AtomTomlLoader {

	public static TomlParseResult load(Path path) throws IOException {
		return Toml.parse(Files.readString(path));
	}

	public static <T> T loadAs(Path path, Class<T> type) throws Exception {
		return AtomTomlDeserializer.fromToml(load(path), type);
	}
}
