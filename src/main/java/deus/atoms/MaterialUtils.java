package deus.atoms;

import net.minecraft.core.block.material.Material;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MaterialUtils {

	public static final Map<String, Material> MATERIALS = new HashMap<>();

	static {
		for (Field f : Material.class.getFields()) {
			if (Material.class.isAssignableFrom(f.getType())) {
				try {
					MATERIALS.put(f.getName(), (Material) f.get(null));
				} catch (IllegalAccessException ignored) {}
			}
		}
	}

}
