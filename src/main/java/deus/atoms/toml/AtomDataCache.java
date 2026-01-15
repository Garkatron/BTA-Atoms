package deus.atoms.toml;


import deus.atoms.toml.types.AtomType;
import deus.atoms.toml.types.CompiledBlock;

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
