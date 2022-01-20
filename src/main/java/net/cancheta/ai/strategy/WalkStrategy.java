package net.cancheta.ai.strategy;

import net.cancheta.ai.task.WalkTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class WalkStrategy extends AIStrategy{
	protected BlockPos target;
	protected ServerCommandSource source;
	protected ServerPlayerEntity player;
	private static final int TICK_AGAIN_EVERY = 20 * 5;
	private int tickAgainCounter = TICK_AGAIN_EVERY;
	
	public WalkStrategy(ServerCommandSource source, ServerPlayerEntity player, BlockPos target) {
		this.target = target;
		this.source = source;
		this.player = player;
	}
	
	public TickResult onGameTick(WalkTask walk) {  //TickResult extends from AIStrategy
		if (tickAgainCounter-- <= 0) {
			tickAgainCounter = TICK_AGAIN_EVERY;
			return TickResult.TICK_AGAIN;
		} else if (walk.walkTowards(source, player, target.getX(), target.getY(), target.getZ())) {
			return TickResult.NO_MORE_WORK;
		} else {
			return TickResult.TICK_HANDLED;
		}
	}
}
