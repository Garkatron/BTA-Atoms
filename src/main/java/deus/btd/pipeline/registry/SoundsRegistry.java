package deus.btd.pipeline.registry;

import deus.btd.Main;
import net.minecraft.core.sound.SoundTypes;


public class SoundsRegistry {
	public static void initialize() {
		SoundTypes.loadSoundsJson(Main.MOD_ID);
		Main.LOGGER.info(Main.MOD_ID + " Sounds Initialized.");

	}
}
