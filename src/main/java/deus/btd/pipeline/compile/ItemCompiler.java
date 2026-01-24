package deus.btd.pipeline.compile;

import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
import deus.btd.pipeline.util.AtomConstants;

import static deus.btd.Main.LOGGER;

public class ItemCompiler {
	public static boolean validate(CompiledItem item) {
		if (item == null) return false;
		if (item.meta.formatVersion != AtomConstants.FORMAT_VERSION) return false;
		return item.data.name != null;
	}

}
