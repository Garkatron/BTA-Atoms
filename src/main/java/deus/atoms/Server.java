package deus.atoms;

import net.fabricmc.api.DedicatedServerModInitializer;

import static deus.atoms.Main.LOGGER;
import static deus.atoms.Main.MOD_ID;

public class Server implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		LOGGER.info(MOD_ID+" Server Initialized");

	}
}
