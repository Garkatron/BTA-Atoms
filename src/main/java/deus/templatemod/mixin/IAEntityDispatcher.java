package deus.templatemod.mixin;

import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = EntityRenderDispatcher.class, remap = false)
public interface IAEntityDispatcher {

	@Accessor("renderers")
	Map<Class<?>, EntityRenderer<?>> getRenderers();

}
