package deus.atoms.mixin;

import deus.atoms.Main;
import net.minecraft.client.render.customatlas.CustomAtlasHandler;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import static deus.atoms.AtomLoader.ATOMS_TEXTURES_PATH;

@Mixin(value = CustomAtlasHandler.class, remap = false)
public class CustomAtlasHandlerMixin {



	@Inject(method = "getTextureOverride",
		at = @At("HEAD"),
		cancellable = true,
		remap = false)
	private static void loadAtomsTextures(TexturePackList texturePackList, String path, CallbackInfoReturnable<BufferedImage> cir) {

		if (path != null && path.contains("/atoms/")) {
			try {

				int atomsIndex = path.indexOf("/atoms/textures/");
				if (atomsIndex != -1) {
					String relativePath = path.substring(atomsIndex + "/atoms/textures/".length());
					relativePath = relativePath.replace(".png", "");

					Path externalPath = ATOMS_TEXTURES_PATH.resolve(relativePath + ".png");

					if (Files.exists(externalPath)) {
						Main.LOGGER.info("Loading external texture from: {}", externalPath);

						BufferedImage image = ImageIO.read(externalPath.toFile());
						if (image != null) {
							cir.setReturnValue(image);
							return;
						}
					} else {
						Main.LOGGER.warn("External texture not found: {}", externalPath);
					}
				}
			} catch (Exception e) {
				Main.LOGGER.error("Failed to load external texture: {}", path, e);
			}
		}
	}
}
