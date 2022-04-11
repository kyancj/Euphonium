package net.cancheta.ai.path;

import net.cancheta.settings.EuphoniumSettingsRoot;
import net.cancheta.settings.PathfindingSetting;

public class WalkingPathfinder extends MovePathFinder {

	@Override
	protected PathfindingSetting loadSettings(EuphoniumSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getWalking();
	}
	
	private final int[] res = new int[14];
	
	@Override
	protected int[] getNeighbours(int currentNode) {
		final int cx = getX(currentNode);
		final int cz = getZ(currentNode);
		final int cy = getY(currentNode);
		final double height = getBlockHeight(cx, cy, cz);
		res[0] = getNeighbour(currentNode, cx, cy + 1, cz);
		res[1] = getNeighbour(currentNode, cx, cy - 1, cz);
		res[2] = getNeighbourIfHeightBelow(currentNode, cx + 1, cy + 1, cz, height);
		res[3] = getNeighbour(currentNode, cx + 1, cy, cz);
		res[4] = getNeighbour(currentNode, cx + 1, cy - 1, cz);
		res[5] = getNeighbourIfHeightBelow(currentNode, cx - 1, cy + 1, cz, height);
		res[6] = getNeighbour(currentNode, cx - 1, cy, cz);
		res[7] = getNeighbour(currentNode, cx - 1, cy - 1, cz);
		res[8] = getNeighbourIfHeightBelow(currentNode, cx, cy + 1, cz + 1, height);
		res[9] = getNeighbour(currentNode, cx, cy, cz + 1);
		res[10] = getNeighbour(currentNode, cx, cy - 1, cz + 1);
		res[11] = getNeighbourIfHeightBelow(currentNode, cx, cy + 1, cz - 1, height);
		res[12] = getNeighbour(currentNode, cx, cy, cz - 1);
		res[13] = getNeighbour(currentNode, cx, cy - 1, cz - 1);
		return res;
	}

	private int getNeighbourIfHeightBelow(int currentNode, int x, int y,
			int z, double height) {
		return getBlockHeight(x, y, z) <= height ? getNeighbour(currentNode, x, y, z) : -1;
	}

	private double getBlockHeight(int cx, int cy, int cz) {
		return world.getCollisionBounds(cx, cy, cz).getMaxY();
	}
}
