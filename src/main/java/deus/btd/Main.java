package deus.btd;

import deus.btd.interfaces.IHasLang;
import deus.btd.interfaces.LanguageEntrypoint;
import deus.btd.mixin.I18nAccessor;
import deus.btd.mixin.LanguageAccessor;
import deus.btd.pipeline.io.AtomPaths;
import deus.btd.pipeline.io.AtomProjectLoader;
import deus.btd.pipeline.project.AtomDataCache;
import deus.btd.pipeline.project.ProjectProcessed;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.compile.types.CompiledItem;
import deus.btd.pipeline.compile.types.fields.Lang;
import deus.btd.pipeline.project.ProjectProcessor;
import deus.btd.pipeline.registry.AtomProjectRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import net.minecraft.core.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static deus.btd.pipeline.registry.AtomProjectRegistry.PROJECTS;
import static net.minecraft.core.entity.EntityDispatcher.stringIdToClassMap;

public class Main implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "better_than_datapacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<Block<?>> blocks = new ArrayList<>();

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialization started.");

		try {
			AtomPaths.createFolders();

			LOGGER.info("Creating folders.");
		} catch (IOException e) {
			LOGGER.error("Error creating folders.");
			throw new RuntimeException(e);
		}

		LOGGER.info("Processing projects.");
		AtomPaths.deletePreviousConfig();
		AtomProjectRegistry.addAll(ProjectProcessor.processProjects(AtomProjectLoader.loadProjects(AtomPaths.DATA)));

	}

	@Override
	public void afterGameStart() {
		LOGGER.info("Registering language entries.");

		for (ProjectProcessed project : PROJECTS) {
			AtomProjectLoader.loadDatapack(project.name(), project.resources().zip());
			AtomProjectLoader.loadTextures(project.resources());
		}

		Minecraft.getMinecraft().texturePackList.refresh();
		runLanguageEntrypoint();
	}

	private void runLanguageEntrypoint() {
		Language language = ((I18nAccessor) I18n.getInstance()).getLanguage();

		FabricLoader.getInstance()
			.getEntrypointContainers("language", LanguageEntrypoint.class)
			.forEach(c -> c.getEntrypoint().register(language));
	}


}
