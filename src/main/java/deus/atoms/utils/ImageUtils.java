package deus.atoms.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static deus.atoms.toml.AtomLoader.ATOMS_TEXTURES_PATH;
import static deus.atoms.toml.AtomLoader.TEXTURE_PATHS;

public class ImageUtils {
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
