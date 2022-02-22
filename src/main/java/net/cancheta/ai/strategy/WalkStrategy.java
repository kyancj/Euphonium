package net.cancheta.ai.strategy;

import java.util.LinkedList;

import net.cancheta.ai.path.Pathfind;
import net.cancheta.ai.task.WalkTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.cancheta.util.KeyRegister;

public class WalkStrategy extends AIStrategy{
	private MinecraftClient mc = MinecraftClient.getInstance();
	protected BlockPos target;
	protected ServerCommandSource source;
	protected ClientPlayerEntity player;
	private static final int TICK_AGAIN_EVERY = 20 * 5;
	private int tickAgainCounter = TICK_AGAIN_EVERY;
	
	public WalkStrategy(ServerCommandSource source, BlockPos target) {
		this.target = target;
		this.source = source;
		this.player = mc.player;
	}
	
	private boolean abort() {
		if (KeyRegister.stop.isPressed()) {
			return true;
		}
		return false;
	}
	
	public TickResult run(WalkTask walk) {
		Pathfind pathfinder = new Pathfind(player.getBlockPos(), target);
		pathfinder.findPath();
		LinkedList<BlockPos> l = pathfinder.getPath();
		BlockPos nextBlock;
		TickResult result = null;
		while(!l.isEmpty()) {
			nextBlock = l.removeFirst();
			this.target = nextBlock;
			result = onGameTick(walk);
			System.out.println(result);
			if (result == TickResult.ABORT) {
				return result;
			}
		}
		return result;
	}
	
	public TickResult onGameTick(WalkTask walk) {  //TickResult extends from AIStrategy
		if (abort()) {
			return TickResult.ABORT;
		} else if (tickAgainCounter-- <= 0) {
			tickAgainCounter = TICK_AGAIN_EVERY;
			return TickResult.TICK_AGAIN;
		} else if (walk.walkTowards(source, player, target.getX(), target.getY(), target.getZ())) {
			return TickResult.NO_MORE_WORK;
		} else {
			return TickResult.TICK_HANDLED;
		}
	}
}
