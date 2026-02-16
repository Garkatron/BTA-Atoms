package deus.btd.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import deus.btd.pipeline.io.AtomPaths;
import deus.btd.pipeline.io.AtomProjectLoader;
import deus.btd.pipeline.project.ProjectProcessor;
import deus.btd.pipeline.registry.AtomProjectRegistry;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

public class BtdCommand implements CommandManager.CommandRegistry {
	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		ArgumentBuilderLiteral<Object> base = ArgumentBuilderLiteral.literal("datapack")
			.requires(source -> ((CommandSource) source).hasAdmin());

		base.then(
			ArgumentBuilderLiteral.literal("reload").executes(c->{
				AtomProjectRegistry.addAll(ProjectProcessor.processProjects(AtomProjectLoader.loadProjects(AtomPaths.DATA)));
				return 0;
			})
		);
		commandDispatcher.register((ArgumentBuilderLiteral<CommandSource>) (Object) base);

	}
}
