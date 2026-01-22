package deus.btd.mixin;

import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(TextureRegistry.class)
public class TextureRegistryMixin {

	@Shadow(remap = false)
	public static HashMap<String, AtlasStitcher> stitcherMap;

	@Shadow(remap = false)
	@Final
	private static Logger LOGGER;

	@Inject(
		method = "getTexture(Lnet/minecraft/core/util/collection/NamespaceID;)Lnet/minecraft/client/render/texture/stitcher/IconCoordinate;",
		at = @At("RETURN"),
		remap = false,
		cancellable = true
	)
	private static void injectCustomTexture(NamespaceID id, CallbackInfoReturnable<IconCoordinate> cir) {
		if (id.namespace().contains("atoms")) {

		}

	}
}
