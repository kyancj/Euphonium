package net.cancheta.ai.path.world;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;

public class Pos {
	
	public static BlockPos ZERO = new BlockPos(0,0,0);

	private Pos() {
	}

	public static BlockPos fromDir(Direction dir) {
		return ZERO.offset(dir);
	}

	public static BlockPos[] fromDir(Direction[] standable) {
		final BlockPos[] res = new BlockPos[standable.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = fromDir(standable[i]);
		}
		return res;
	}

	public static BlockPos minPos(BlockPos p1, BlockPos p2) {
		return new BlockPos(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(
				p1.getZ(), p2.getZ()));
	}

	public static BlockPos maxPos(BlockPos p1, BlockPos p2) {
		return new BlockPos(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(
				p1.getZ(), p2.getZ()));
	}

	public static double length(double dx, double dy, double dz) {
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public static String niceString(BlockPos pos) {
		return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}
}
