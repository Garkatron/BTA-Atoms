package deus.utils.initializers;

import deus.templatemod.Main;
import deus.templatemod.mixin.IAEntityDispatcher;
import deus.utils.annotations.RegisterEntityRenderer;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.core.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

public class EntityModelInitializer {

	@SuppressWarnings("unchecked")
	public static void initialize(String prefix, EntityRenderDispatcher dispatcher) {
		Reflections reflections = new Reflections(prefix);

		for (Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterEntityRenderer.class)) {
			RegisterEntityRenderer annotation = clazz.getAnnotation(RegisterEntityRenderer.class);

			if (annotation != null && Entity.class.isAssignableFrom(clazz)) {
				try {
					EntityRenderer<?> rendererInstance;

					if (annotation.model() != ModelBase.class) {
						ModelBase modelInstance = annotation.model().getDeclaredConstructor().newInstance();
						rendererInstance = annotation.renderer()
							.getDeclaredConstructor(annotation.model())
							.newInstance(modelInstance);
					} else {
						rendererInstance = annotation.renderer()
							.getDeclaredConstructor()
							.newInstance();
					}
					addEntityModel(dispatcher, (Class<? extends Entity>) clazz, rendererInstance);

					Main.LOGGER.info("@Registered Renderer & Model for entity -> {}", clazz.getName());
				} catch (Exception e) {
					Main.LOGGER.error("Failed to register Renderer & Model for entity -> {}", clazz.getSimpleName(), e);
				}
			}
		}
	}

	public static void addEntityModel(EntityRenderDispatcher dispatcher, @NotNull Class<? extends Entity> clazz, EntityRenderer<?> renderer) {
		renderer.init(dispatcher);
		((IAEntityDispatcher) dispatcher).getRenderers().put(clazz, renderer);
	}
}
