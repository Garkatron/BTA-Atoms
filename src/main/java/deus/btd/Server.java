package deus.btd;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static deus.btd.Main.LOGGER;
import static deus.btd.Main.MOD_ID;

@Environment(EnvType.SERVER)
public class Server implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		LOGGER.info(MOD_ID + " Server Initialized");
	}
}
