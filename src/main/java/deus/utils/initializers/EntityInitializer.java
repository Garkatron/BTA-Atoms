package deus.utils.initializers;

import deus.templatemod.Main;
import deus.utils.annotations.RegisterEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.collection.NamespaceID;
import org.reflections.Reflections;
import turniplabs.halplibe.helper.EntityHelper;

public class EntityInitializer {
	public static void initialize(String prefix) {
		Reflections reflections = new Reflections(prefix);
		for (Class<?> clazz : reflections.getTypesAnnotatedWith(RegisterEntity.class)) {
			RegisterEntity annotation = clazz.getAnnotation(RegisterEntity.class);
			if (annotation != null) {
				if (TileEntity.class.isAssignableFrom(clazz)) {
					EntityHelper.createTileEntity((Class<? extends TileEntity>) clazz, NamespaceID.getPermanent(annotation.modId(), annotation.id()));
				} else {
					EntityHelper.createEntity((Class<? extends Entity>) clazz, NamespaceID.getPermanent(annotation.modId(), annotation.id()), annotation.name());
				}
				Main.LOGGER.info("@Registered entity -> {}", annotation.name());
			}
		}
	}
}
