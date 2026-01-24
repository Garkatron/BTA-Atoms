package deus.btd.pipeline.io;

import deus.btd.pipeline.project.AtomDataCache;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static deus.btd.Main.LOGGER;

public final class AtomPaths {
    public static final Path ROOT = Paths.get(Minecraft.getMinecraft().getMinecraftDir().getPath(), "btd");
    public static final Path DATA = ROOT.resolve("data");
    public static final Path CONFIG = Paths.get(
        Minecraft.getMinecraft().getMinecraftDir().getPath(),
        "config/better_than_datapacks.cfg"
    );


	public static void createFolders() throws IOException {
		Files.createDirectories(DATA);
	}

	public static void deletePreviousConfig() {
		if (CONFIG.toFile().exists()) {
			LOGGER.info("Deleting previous config.");
			boolean result = AtomPaths.CONFIG.toFile().delete();
			LOGGER.info("Deleted: {}", result);
		}
	}
}
