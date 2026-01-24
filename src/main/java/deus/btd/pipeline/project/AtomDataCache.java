package deus.btd.pipeline.project;


import deus.btd.pipeline.compile.types.AtomType;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class AtomDataCache {
	public static Map<AtomType, List<?>> ATOMS = null;

	public static void clear() {
		if (ATOMS != null) {
			ATOMS.clear();
			ATOMS = null;
		}
	}

}
