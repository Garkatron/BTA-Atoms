package deus.utils.annotations;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.ModelBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterEntityRenderer {
	@NotNull Class<? extends EntityRenderer> renderer();
	@Nullable Class<? extends ModelBase> model() default ModelBase.class;
}
