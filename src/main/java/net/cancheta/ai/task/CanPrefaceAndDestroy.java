package net.cancheta.ai.task;

import net.cancheta.ai.AIHelper;
import net.cancheta.ai.path.world.BlockSets;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface CanPrefaceAndDestroy extends CanWorkWhileApproaching {
	static final Logger LOGGER = LogManager.getLogger(CanPrefaceAndDestroy.class);
	// Maximum distance for aiming at blocks to destroy.
	static final int MAX_PREDESTROY_DISTANCE = 4;

	public default boolean doApproachWork(AIHelper helper) {
		final List<BlockPos> positions = getPredestroyPositions(helper);
		if (!positions.isEmpty()) {
			LOGGER.debug("Next tasks scheduled request the destruction of {}", positions);
		}
		for (final BlockPos pos : positions) {
			if (!BlockSets.AIR.isAt(helper.getWorld(), pos)
					&& pos.getSquaredDistance(helper
							.getPlayerPosition()) < MAX_PREDESTROY_DISTANCE
							* MAX_PREDESTROY_DISTANCE) {
				LOGGER.debug("Selected position {} for destruction", pos);
				helper.faceAndDestroy(pos);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a list that this task faces and destroys that could already be
	 * mined before arriving at the target location.
	 * 
	 * @param helper
	 *            The AI helper.
	 * @return A list of block positions, preferably ordered the way the task
	 *         destroys them.
	 */
	List<BlockPos> getPredestroyPositions(AIHelper helper);

}
