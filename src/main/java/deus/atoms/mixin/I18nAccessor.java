package deus.atoms.mixin;


import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(I18n.class)
public interface I18nAccessor {
	@Accessor("currentLanguage")
	Language getLanguage();
}
