package deus.atoms.mixin;

import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.core.util.collection.NamespaceID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.nio.file.Paths;

import static deus.atoms.AtomLoader.ATOMS_TEXTURES_PATH;

@Mixin(AtlasStitcher.class)
public class AtlasStitcherMixin {



}
