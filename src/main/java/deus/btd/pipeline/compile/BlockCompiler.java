package deus.btd.pipeline.compile;

import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.util.AtomConstants;

public class BlockCompiler {
	public static boolean validate(CompiledBlock block) {
		if (block == null) return false;
		if (block.meta.formatVersion != AtomConstants.FORMAT_VERSION) return false;
		return block.data.name != null;
	}

}
