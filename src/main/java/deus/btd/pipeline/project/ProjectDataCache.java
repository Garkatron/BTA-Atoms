package deus.btd.pipeline.project;


import deus.btd.pipeline.compile.types.AtomType;

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
