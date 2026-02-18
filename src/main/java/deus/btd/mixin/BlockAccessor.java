package deus.btd.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.block.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Block.class, remap = false)
public interface BlockAccessor {

	// ===== core =====
	@Accessor("logic")
	void setLogic(BlockLogic logic);

	@Accessor("logic")
	BlockLogic getLogic();

	// ===== hardness =====
	@Accessor("blockHardness")
	void setBlockHardness(float v);

	@Accessor("blockHardness")
	float getBlockHardness();

	// ===== blast =====
	@Accessor("blastResistance")
	void setBlastResistance(float v);

	@Accessor("blastResistance")
	float getBlastResistance();

	// ===== friction =====
	@Accessor("friction")
	void setFriction(float v);

	@Accessor("friction")
	float getFriction();

	// ===== light emission =====
	@Accessor("emission")
	void setEmission(int v);

	@Accessor("emission")
	int getEmission();

	// ===== light block =====
	@Accessor("lightBlock")
	void setLightBlock(Integer v);

	@Accessor("lightBlock")
	Integer getLightBlock();

	// ===== sound =====
	@Accessor("blockSound")
	void setBlockSound(BlockSound s);

	@Accessor("blockSound")
	BlockSound getBlockSound();

	// ===== flags =====
	@Accessor("immovable")
	void setImmovable(boolean v);

	@Accessor("immovable")
	boolean getImmovable();

	@Accessor("isLitInteriorSurface")
	void setLitInteriorSurface(boolean v);

	@Accessor("isLitInteriorSurface")
	boolean getLitInteriorSurface();

	@Accessor("shouldTick")
	void setShouldTick(boolean v);

	@Accessor("shouldTick")
	boolean getShouldTick();

	// ===== color =====
	@Accessor("overrideColor")
	void setOverrideColor(MaterialColor c);

	@Accessor("overrideColor")
	MaterialColor getOverrideColor();
}
