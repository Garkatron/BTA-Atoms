package deus.btd.toml;


import deus.btd.toml.types.AtomType;

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
