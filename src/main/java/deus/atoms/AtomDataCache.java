package deus.atoms;


import org.tomlj.TomlParseResult;
import java.util.List;

public class AtomDataCache {
	public static List<TomlParseResult> ATOMS = null;

	public static void clear() {
		if (ATOMS != null) {
			ATOMS.clear();
			ATOMS = null;
		}
	}
}
