package deus.atoms.mixin;

import net.minecraft.core.lang.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Properties;

@Mixin(Language.class)
public interface LanguageAccessor {
	@Accessor("entries")
	Properties getEntries();
}
