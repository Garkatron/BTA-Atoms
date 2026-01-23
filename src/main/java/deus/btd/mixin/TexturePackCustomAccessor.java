package deus.btd.mixin;

import net.minecraft.client.render.texturepack.TexturePackCustom;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.awt.image.BufferedImage;

@Mixin(TexturePackCustom.class)
public interface TexturePackCustomAccessor {

	@Accessor(value = "thumbnailBuffer", remap = false)
	@NotNull
	BufferedImage getThumbnailBuffer();

	@Accessor(value = "thumbnailBuffer", remap = false)
	void setThumbnailBuffer(@NotNull BufferedImage image);
}
