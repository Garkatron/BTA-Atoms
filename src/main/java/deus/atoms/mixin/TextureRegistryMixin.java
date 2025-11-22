package deus.atoms.mixin;


import deus.atoms.Main;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(net.minecraft.client.render.texture.stitcher.TextureRegistry.class)
public class TextureRegistryMixin  {

	@Shadow
	public static HashMap<String, AtlasStitcher> stitcherMap;


	@Inject(method = "getTexture(Lnet/minecraft/core/util/collection/NamespaceID;)Lnet/minecraft/client/render/texture/stitcher/IconCoordinate;", at = @At("RETURN"), remap = false, cancellable = true)
	private static void test(NamespaceID id, CallbackInfoReturnable<IconCoordinate> cir) {

		//System.out.println("DADADA: "+id.value);


		String str = id.value().substring(1);
		String[] split2 = str.split("/");
		AtlasStitcher atlas = stitcherMap.get(split2[0]);

		if (id.value().startsWith("$")) {

			System.out.println("[GET TEXTURE]: $");
			IconCoordinate result = ((AtlasStitcherAccessor) atlas).callGetTexture(NamespaceID.getTemp(Main.MOD_ID, str.replace("¿",":")));

			cir.setReturnValue(result);
		} else if (id.value().startsWith("&")) {

			System.out.println("[GET TEXTURE]: &");

			IconCoordinate result = ((AtlasStitcherAccessor) atlas).callGetTexture(NamespaceID.getTemp(Main.MOD_ID, str));
			cir.setReturnValue(result);

		}


	}

	/*
	@Inject(method = "hasTexture(Lnet/minecraft/core/util/collection/NamespaceID;)Z", at=@At("HEAD"), remap = false, cancellable = true)
	private static void test2(NamespaceID id, CallbackInfoReturnable<Boolean> cir) {

	}
	*/


}
