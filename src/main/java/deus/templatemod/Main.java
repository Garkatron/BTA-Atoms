package deus.templatemod;

import deus.templatemod.entry_points.Sounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.sound.SoundTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class Main implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "templatemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	static {
		// ConfigManager.makeConfig();
	}


	@Override
    public void onInitialize() {
		LOGGER.info(MOD_ID+" Core Initialized.");
    }



	@Override
	public void beforeGameStart() {
		Sounds.initialize();

		try {
			TextureRegistry.initializeAllFiles(MOD_ID, TextureRegistry.blockAtlas, true);
		} catch (Exception var2) {
			LOGGER.warn("{}: Failed to fully initialize assets, some issue may occur!",MOD_ID,  var2);
		}
	}

	@Override
	public void afterGameStart() {

	}
}
