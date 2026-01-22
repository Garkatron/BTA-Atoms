package deus.btd;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundRepository;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static deus.btd.Main.MOD_ID;
import static deus.btd.Main.LOGGER;

@Environment(EnvType.CLIENT)
public class Client  implements ClientStartEntrypoint, ClientModInitializer  {
	@Override
	public void onInitializeClient() {
		SoundRepository.registerNamespace(MOD_ID);
		LOGGER.info(MOD_ID + " Client Initialized");
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}
}
