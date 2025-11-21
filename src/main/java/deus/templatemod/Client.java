package deus.templatemod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.sound.SoundRepository;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static deus.templatemod.Main.MOD_ID;
import static deus.templatemod.Main.LOGGER;

public class Client  implements ClientStartEntrypoint, ClientModInitializer  {
	@Override
	public void onInitializeClient() {
		SoundRepository.registerNamespace(MOD_ID);
		LOGGER.info(MOD_ID+" Client Initialized");
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}
}
