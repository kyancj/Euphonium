package net.cancheta.ai.task.error;

import net.minecraft.util.math.BlockPos;

public class PositionTaskError extends TaskError {

	private final BlockPos expectedPosition;

	public PositionTaskError(BlockPos expectedPosition) {
		super("Not standing on " + expectedPosition.getX() + ", " + expectedPosition.getY() + ", " + expectedPosition.getZ() + ".");
		this.expectedPosition = expectedPosition;
	}

	public PositionTaskError(int x, int y, int z) {
		this(new BlockPos(x, y, z));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (expectedPosition == null ? 0 : expectedPosition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PositionTaskError other = (PositionTaskError) obj;
		if (expectedPosition == null) {
			if (other.expectedPosition != null) {
				return false;
			}
		} else if (!expectedPosition.equals(other.expectedPosition)) {
			return false;
		}
		return true;
	}

}
