package deus.atoms;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.sound.SoundRepository;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static deus.atoms.Main.MOD_ID;
import static deus.atoms.Main.LOGGER;

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
