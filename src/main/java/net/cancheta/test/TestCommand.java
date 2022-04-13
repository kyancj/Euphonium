package net.cancheta.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.util.math.BlockPos;
//import net.cancheta.ai.command.IAIControllable;
//import net.cancheta.ai.command.SafeStrategyRule;
//import net.cancheta.ai.commands.Commands;
import net.cancheta.ai.path.GoToPathfinder;
import net.cancheta.ai.path.GoToPathfinderDestructive;
import net.cancheta.ai.strategy.PathFinderStrategy;
//import net.cancheta.ai.path.GoToPathfinderDestructive;
//import net.cancheta.ai.strategy.PathFinderStrategy;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TestCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
    	dispatcher.register(
    			CommandManager.literal("pathfind").then(
    					CommandManager.argument("to", BlockPosArgumentType.blockPos())
								.executes(context -> runPathfind(context, false))
						.then(CommandManager.literal("destructive").executes(context -> runPathfind(context, true)))
				)
		);
	}
	
	private static int runPathfind(CommandContext<ServerCommandSource> context, boolean destroy) throws CommandSyntaxException {
		BlockPos position = BlockPosArgumentType.getBlockPos(context, "to");
//		return context.getSource().requestUseStrategy(new PathFinderStrategy(destroy ? new GoToPathfinderDestructive(position) : new GoToPathfinder(position), "Go to "
//				+ position.getX() + "," + position.getY() + "," + position.getZ()), destroy ? SafeStrategyRule.DEFEND_MINING : SafeStrategyRule.DEFEND);
		new PathFinderStrategy(destroy ? new GoToPathfinderDestructive(position) : new GoToPathfinder(position), "Go to "
				+ position.getX() + "," + position.getY() + "," + position.getZ());
		return 1;
	}
}
