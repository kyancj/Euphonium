package net.cancheta.settings;

import net.cancheta.ai.path.world.BlockSet;

public class PathfindingSetting {
	private BlockSet allowedGround;
	
	/**
	 * 'Ground' allowed to build upwards. Should include air.
	 */
	private BlockSet allowedGroundWhenUpwards;
	
	private BlockSet footWalkThrough;
	
	private BlockSet headWalkThrough;
	
	private BlockSet upwardsBuildBlocks;
	
	private String help = "With this, you can add your custom mod blocks to the path finder. Modify this with care. Delete the whole entry to reset to default.";
	
	public PathfindingSetting() {
		// for json parsing
	}
	
	public PathfindingSetting(BlockSet allowedGround,
			BlockSet allowedGroundWhenUpwards, BlockSet footWalkThrough,
			BlockSet headWalkThrough, BlockSet upwardsBuildBlocks) {
		super();
		this.allowedGround = allowedGround;
		this.allowedGroundWhenUpwards = allowedGroundWhenUpwards;
		this.footWalkThrough = footWalkThrough;
		this.headWalkThrough = headWalkThrough;
		this.upwardsBuildBlocks = upwardsBuildBlocks;
	}

	public BlockSet getAllowedGround() {
		return allowedGround;
	}
	public BlockSet getFootWalkThrough() {
		return footWalkThrough;
	}
	
	public BlockSet getHeadWalkThrough() {
		return headWalkThrough;
	}
	
	public BlockSet getAllowedGroundWhenUpwards() {
		return allowedGroundWhenUpwards;
	}
	
	public BlockSet getUpwardsBuildBlocks() {
		return upwardsBuildBlocks;
	}
}
