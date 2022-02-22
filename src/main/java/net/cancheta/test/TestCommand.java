package net.cancheta.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;


public class TestCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandManager.literal("walk")
			.executes((context) -> {
					return run((ServerCommandSource)context.getSource());
		}));
	}
	
	private static int run(ServerCommandSource source) throws CommandSyntaxException{
		boolean done = false;
		return 1;
	}
}
