package net.cancheta.ai.path;

import net.minecraft.util.math.BlockPos;

public class PathNode {
	public static final int VHCost = 10;
	
	private int hCost = 0;
	private int gCost = 0;
	private int finalCost = 0;
	private BlockPos block;
	private PathNode parent;
	private boolean isPassable;
	
	PathNode(BlockPos block){
		this.block = block;
	}
	
	public int getFinalCost() {
		return finalCost;
	}
	
	public boolean getIsPassable() {
		return isPassable;
	}
	
	public BlockPos getBlock() {
		return block;
	}
}
