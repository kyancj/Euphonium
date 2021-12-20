package net.cancheta.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPredicateArgumentType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;

public class MineCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated){
		dispatcher.register(CommandManager.literal("mine").then(CommandManager.argument("Block", BlockPredicateArgumentType.blockPredicate()).executes(context -> {return run((ServerCommandSource)context.getSource());})));
	}
	
	private static int run(ServerCommandSource source) throws CommandSyntaxException{
		return 1;
	}
}
