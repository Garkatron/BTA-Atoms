package deus.atoms;


import deus.atoms.utils.CompiledBlock;
import org.tomlj.TomlParseResult;
import java.util.List;

public class AtomDataCache {
	public static List<CompiledBlock> ATOMS = null;

	public static void clear() {
		if (ATOMS != null) {
			ATOMS.clear();
			ATOMS = null;
		}
	}
}
