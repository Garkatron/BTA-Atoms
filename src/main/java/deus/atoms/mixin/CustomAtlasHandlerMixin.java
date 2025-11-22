package deus.atoms.mixin;

import deus.atoms.AtomLoader;
import net.minecraft.client.render.customatlas.CustomAtlasHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@Mixin(CustomAtlasHandler.class)
public class CustomAtlasHandlerMixin {

	@Shadow
	private static Map<String, BufferedImage> textureOverrides;

	@Inject(method = "beforeRefreshTextures()V", at = @At("TAIL"), remap = false)
	private static void loadAtomTextures(CallbackInfo ci) {
		Path folder = Paths.get(AtomLoader.ATOMS_TEXTURES_PATH.toString(), "block");

		System.out.println("[Atoms] Loading from: " + folder.toAbsolutePath());

		if (!Files.exists(folder)) {
			try {
				Files.createDirectories(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		try (Stream<Path> files = Files.list(folder)) {
			files.filter(p -> p.toString().endsWith(".png"))
				.forEach(png -> {
					try {
						String name = png.getFileName().toString().replace(".png", "");
						BufferedImage img = ImageIO.read(png.toFile());
						String path = "/assets/atoms/textures/block/" + name + ".png";
						textureOverrides.put(path, img);
						System.out.println("[Atoms] Loaded: " + path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
