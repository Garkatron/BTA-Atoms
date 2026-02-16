package deus.btd.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockAccessor {
	@Accessor(value = "logic", remap = false)
	void setLogic(BlockLogic logic);
}
