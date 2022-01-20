package net.cancheta.ai.task;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class WalkTask {
	private final Key forward = InputUtil.fromKeyCode(87, 0);
	private final double MIN_DISTANCE_ERROR = 0.05;
	public WalkTask() {
	}
	
	public boolean walkTowards(ServerCommandSource source, ServerPlayerEntity player, double x, double y, double z) {
		final double dx = x - player.getX();
		final double dz = z - player.getZ();
		final double distTo = Math.sqrt((float) (dx * dx + dz * dz));
		boolean arrived = distTo <= MIN_DISTANCE_ERROR;
		
		if (!arrived) {
			player.lookAt(source.getEntityAnchor(), new Vec3d(x, y, z));
			KeyBinding.setKeyPressed(forward, true);
			return false;
		}
		KeyBinding.setKeyPressed(forward, false);
		return true;
	}
}
