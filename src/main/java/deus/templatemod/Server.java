package deus.templatemod;

import net.fabricmc.api.DedicatedServerModInitializer;

import static deus.templatemod.Main.LOGGER;
import static deus.templatemod.Main.MOD_ID;

public class Server implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		LOGGER.info(MOD_ID+" Server Initialized");

	}
}
