package deus.atoms.entry_points;

import deus.atoms.Main;
import net.minecraft.core.sound.SoundTypes;


public class Sounds {
	public static void initialize() {
		// SoundTypes.register(MOD_ID+".paper.put" );
		SoundTypes.loadSoundsJson(Main.MOD_ID);
		Main.LOGGER.info(Main.MOD_ID+" Sounds Initialized.");

	}
}
