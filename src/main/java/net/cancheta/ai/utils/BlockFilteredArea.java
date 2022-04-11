package net.cancheta.ai.utils;

import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.path.world.WorldData;

public class BlockFilteredArea<W extends WorldData> extends AbstractFilteredArea<W> {
	private final BlockSet containedBlocks;

	public BlockFilteredArea(BlockArea<W> base,
			BlockSet containedBlocks) {
		super(base);
		this.containedBlocks = containedBlocks;
	}

	@Override
	protected boolean test(W world, int x, int y, int z) {
		return containedBlocks.isAt(world, x, y, z);
	}
}
