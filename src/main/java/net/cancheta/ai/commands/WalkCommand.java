package net.cancheta.ai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.cancheta.ai.command.IAIControllable;
import net.cancheta.ai.command.SafeStrategyRule;
import net.cancheta.ai.path.GoToPathfinder;
import net.cancheta.ai.path.GoToPathfinderDestructive;
import net.cancheta.ai.strategy.PathFinderStrategy;
import net.minecraft.util.math.BlockPos;

public class WalkCommand {
    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
    	dispatcher.then(
				Commands.literal("pathfind").then(
						Commands.argument("to", BlockPosArgumentType.blockPos())
								.executes(context -> runPathfind(context, false))
						.then(Commands.literal("destructive").executes(context -> runPathfind(context, true)))
				)
		);
	}

	private static int runPathfind(CommandContext<IAIControllable> context, boolean destroy) {
		BlockPos position = Commands.getBlockPos(context, "to");
		return context.getSource().requestUseStrategy(new PathFinderStrategy(destroy ? new GoToPathfinderDestructive(position) : new GoToPathfinder(position), "Go to "
				+ position.getX() + "," + position.getY() + "," + position.getZ()), destroy ? SafeStrategyRule.DEFEND_MINING : SafeStrategyRule.DEFEND);
	}
}