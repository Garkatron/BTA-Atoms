package deus.atoms.mixin;

import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.util.collection.NamespaceID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.jetbrains.annotations.NotNull;

@Mixin(AtlasStitcher.class)
public interface AtlasStitcherAccessor {

	@Invoker(value = "getTexture", remap = false)
	@NotNull
	IconCoordinate callGetTexture(@NotNull NamespaceID id);
}
