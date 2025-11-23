package deus.atoms;

import net.minecraft.client.Minecraft;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static deus.atoms.Main.MOD_ID;



public class AtomLoader {

	public static final Map<String, String> TEXTURE_PATHS = new HashMap<>();

	public static final Path ATOMS_PATH = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "atoms");
	public static final Path ATOMS_BLOCKS_PATH = Paths.get(ATOMS_PATH.toString(), "blocks");
	public static final Path ATOMS_TEXTURES_PATH = Paths.get(ATOMS_PATH.toString(), "assets", "textures");

	public static List<TomlParseResult> loadAllAtoms() throws IOException {
		List<TomlParseResult> results = new ArrayList<>();

		if (!Files.exists(ATOMS_BLOCKS_PATH)) {
			Main.LOGGER.warn("0 Atoms found at {}.", ATOMS_BLOCKS_PATH);
			return results;
		}

		try (Stream<Path> paths = Files.walk(ATOMS_BLOCKS_PATH)) {
			List<Path> tomlFiles = paths
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".toml"))
				.collect(Collectors.toList());

			for (Path tomlFile : tomlFiles) {
				results.add(loadToml(tomlFile));
			}
		}

		return results;
	}

	public static TomlParseResult loadAtom(String subpath) throws IOException {

		return loadToml(Paths.get(ATOMS_BLOCKS_PATH.toString(), subpath));
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

	public static String loadB64PNG(String base64, String blockName, String faceName) {
		try {
			if (base64.contains(",")) base64 = base64.substring(base64.indexOf(',') + 1);
			byte[] decoded = Base64.getDecoder().decode(base64);

			String fileName = faceName + ".png";

			Path outPath = Paths.get(ATOMS_TEXTURES_PATH.toString(), "block", blockName, fileName);

			File outFile = outPath.toFile();
			outFile.getParentFile().mkdirs();

			try (FileOutputStream fos = new FileOutputStream(outFile)) {
				fos.write(decoded);
			}

			String relativePath =  blockName + "/" + faceName;
			TEXTURE_PATHS.put(blockName + "_" + faceName, relativePath);

			return relativePath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
