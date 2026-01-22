package deus.btd.entry_points;

import deus.btd.Main;
import net.minecraft.core.sound.SoundTypes;


public class Sounds {
	public static void initialize() {
		SoundTypes.loadSoundsJson(Main.MOD_ID);
		Main.LOGGER.info(Main.MOD_ID + " Sounds Initialized.");

	}
}
