package deus.atoms.toml.project;


import deus.atoms.toml.types.AtomType;

import java.util.List;
import java.util.Map;

public class ProjectDataCache {
	public Map<AtomType, List<?>> ATOMS = null;

	public void clear() {
		if (ATOMS != null) {
			ATOMS.clear();
			ATOMS = null;
		}
	}
}
