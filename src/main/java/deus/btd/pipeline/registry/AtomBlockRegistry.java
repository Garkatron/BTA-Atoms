package deus.btd.pipeline.registry;

import deus.btd.pipeline.compile.types.CompiledBlock;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;

import java.util.HashMap;
import java.util.Map;

public final class AtomBlockRegistry {

	private static final Map<NamespaceID, CompiledBlock> ATOMS = new HashMap<>();

	private AtomBlockRegistry() {}

	// ---------- register ----------
	public static void add(String namespaceId, CompiledBlock block) {
		ATOMS.put(parse(namespaceId), block);
	}

	public static void add(NamespaceID id, CompiledBlock block) {
		ATOMS.put(id.makePermanent(), block);
	}

	// ---------- lookup ----------
	public static CompiledBlock get(String namespaceId) {
		return ATOMS.get(parse(namespaceId));
	}

	public static CompiledBlock get(NamespaceID id) {
		return ATOMS.get(id);
	}

	public static boolean contains(String namespaceId) {
		return ATOMS.containsKey(parse(namespaceId));
	}

	public static boolean contains(NamespaceID id) {
		return ATOMS.containsKey(id);
	}

	// ---------- util ----------
	private static NamespaceID parse(String id) {
		try {
			if (!id.contains(":"))
				id = "btd:" + id;
			return NamespaceID.getPermanent(id);
		} catch (HardIllegalArgumentException e) {
			throw new RuntimeException("Invalid namespace: " + id, e);
		}
	}
}
