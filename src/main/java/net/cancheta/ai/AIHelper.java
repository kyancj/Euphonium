package net.cancheta.ai;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Predicate;

import net.cancheta.ai.strategy.AIStrategy;
import net.cancheta.ai.task.BlockHalf;
import net.cancheta.ai.tools.ToolRater;
import net.cancheta.settings.EuphoniumSettings;
import net.cancheta.settings.SaferuleSettings;
import net.cancheta.stats.StatsManager;
import net.cancheta.ai.input.KeyboardInputController;
import net.cancheta.ai.input.KeyboardInputController.KeyType;
import net.cancheta.ai.path.world.BlockBounds;
import net.cancheta.ai.path.world.BlockBoundsCache;
import net.cancheta.ai.path.world.Pos;
import net.cancheta.ai.path.world.WorldData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
//import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;

public abstract class AIHelper {
	private static final Marker MARKER_FACING = MarkerManager
			.getMarker("facing");
	private static final Logger LOGGER = LogManager.getLogger(AIHelper.class);
	
	private static final double WALK_PER_STEP = 4.3 / 20;
	private static final double MIN_DISTANCE_ERROR = 0.05;
	
	private static MinecraftClient mc = MinecraftClient.getInstance();
	
	private static WorldData minecraftWorld;
	
	private boolean objectMouseOverInvalidated;
	
	private Input resetMovementInput;
	
	private HashMap<KeyType, KeyboardInputController> keys = new HashMap<KeyType, KeyboardInputController>();
	
	private final StatsManager stats = new StatsManager();
	
	public AIHelper() {
		for (KeyType key : KeyType.values()) {
			keys.put(key, new KeyboardInputController(mc, key));
		}
	}
	
	public MinecraftClient getClient() {
		return mc;
	}
	
	public void invalidateObjectMouseOver() {
		objectMouseOverInvalidated = true;
	}
	
	public HitResult getObjectMouseOver() {
		if (objectMouseOverInvalidated) {
			objectMouseOverInvalidated = false;
			getClient().gameRenderer.updateTargetedEntity(1.0F);
		}
		return getClient().crosshairTarget;
	}
	
	public WorldData getWorld() {
		return minecraftWorld;
	}
	
	public abstract AIStrategy getResumeStrategy();
	
	public boolean isAlive() {
		return getClient().player != null && getClient().player.getHealth() > 0.0F;
	}
	
	public List<Entity> getEntities(int dist, Predicate<Entity> selector){
		List<Entity> entities = getClient().world.getOtherEntities(
				getClient().getCameraEntity(),
				getClient().getCameraEntity().getBoundingBox()
				.expand(-dist, -dist, -dist)
				.expand(dist, dist, dist)
				.expand(1),
				selector);
		
		return entities;
	}
	
	public Entity getClosestEntity(int dist, Predicate<Entity> selector) {
		final List<Entity> entities = getEntities(dist, selector);
		
		double mindist = Double.MAX_VALUE;
		Entity found = null;
		for (final Entity e : entities) {
			final double mydist = e.distanceTo(getClient().player);
			if (mydist < mindist) {
				found = e;
				mindist = mydist;
			}
		}
		return found;
	}
	
	
	private float closestRotation(float f) {
		float halfRot = 180;
		float fullRot = halfRot * 2;
		return (((f + halfRot) % fullRot + fullRot) % fullRot) - halfRot;
	}
	
	public boolean isStandingOn(int x, int y, int z) {
		// boolean isFence = blockIsOneOf(getBlock(x, y - 1, z),
		// FenceBuildTask.BLOCKS);
		return Math.abs(x + 0.5 - getClient().player.getX()) < 0.2
				&& Math.abs(z + 0.5 - getClient().player.getZ()) < 0.2
				&& Math.abs(getClient().player.getBoundingBox().minY - y) < 0.52;
	}
	
	public void faceAndDestroy(final BlockPos pos) {
		if (!isFacingBlock(pos)) {
			LOGGER.debug("Attempt to face {} to destroy it", pos);
			faceBlock(pos);
		}

		if (isFacingBlock(pos)) {
			LOGGER.debug("Facing block at {} and destroying it", pos);
			selectToolFor(pos);
			overrideAttack();
			stats.markIntentionalBlockBreak(pos);
		}
	}
	
	public void faceAndDestroyWithHangingBlock(final BlockPos pos) {
		faceAndDestroy(pos);
		if (!isFacingBlock(pos)) {
			// Check if there is a block hanging here.
			for (Direction d : Direction.values()) {
				BlockPos offseted = pos.offset(d);
				if (isFacingBlock(offseted)) {
					BlockPos hanging = getWorld().getHangingOnBlock(offseted);
					if (hanging != null && hanging.equals(pos)) {
						LOGGER.debug("Found a hanging block at {} that is in the way to destroy {}", offseted, pos);
						overrideAttack();
					}
				}
			}
		}
	}
	
	public BlockPos getPlayerPosition() {
		return getWorld().getPlayerPosition();
	}
	
	public static class ToolRaterResult {
		private final int bestSlot;
		private final float bestSlotRating;
		
		public ToolRaterResult(int bestSlot, float bestSlotRating) {
			super();
			this.bestSlot = bestSlot;
			this.bestSlotRating = bestSlotRating;
		}

		public int getBestSlot() {
			return bestSlot;
		}

		public float getBestSlotRating() {
			return bestSlotRating;
		}

		public boolean wasSuccessful() {
			return bestSlotRating > 0;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + bestSlot;
			result = prime * result + Float.floatToIntBits(bestSlotRating);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ToolRaterResult other = (ToolRaterResult) obj;
			if (bestSlot != other.bestSlot)
				return false;
			if (Float.floatToIntBits(bestSlotRating) != Float
					.floatToIntBits(other.bestSlotRating))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ToolRaterResult [bestSlot=" + bestSlot
					+ ", bestSlotRating=" + bestSlotRating + "]";
		}
	}
	
	public ToolRaterResult selectToolFor(final BlockPos pos) {
		ToolRater toolRater = EuphoniumSettings.getSettings().getToolRater();
		return selectToolFor(pos, toolRater);
	}
	
	public ToolRaterResult selectToolFor(final BlockPos pos, ToolRater rater) {
		ToolRaterResult res = searchToolFor(pos, rater);
		getClient().player.getInventory().selectedSlot = res.getBestSlot();
		return res;
	}
	
	public ToolRaterResult searchToolFor(final BlockPos pos, ToolRater rater) {
		int bestRatingSlot = getClient().player.getInventory().selectedSlot;
		if (bestRatingSlot < 0 || bestRatingSlot >= 9) {
			bestRatingSlot = 0;
		}
		int block = pos == null ? -1 : getWorld().getBlockStateId(pos);
		float bestRating = rater.rateTool(
				getClient().player.getInventory().getStack(bestRatingSlot), block);
		String bestToolName = null;
		float hardness = 0;
		Material material = null;
		String resistance = null;
		try {
			material = getClient().world.getBlockState(pos).getMaterial();
			hardness = getClient().world.getBlockState(pos).getBlock().getHardness();
			bestToolName = searchForTool(material, hardness);
			//System.out.println("BestToolName:" + bestToolName + "for block: " + getMinecraft().world.getBlockState(pos).getBlock().toString());
		} catch (Exception ex) {
				//For fishing etc.
			}
		
		for (int i = 0; i < 9; ++i) {
			float rating = rater.rateTool(
					getClient().player.getInventory().getStack(i), block);
			try {
				Item currSlot = getClient().player.getInventory().getStack(i).getItem();
				String currSlotName = currSlot.getName().getString();
				int currSlotDurability = getClient().player.getInventory().getStack(i).getMaxDamage() -
						getClient().player.getInventory().getStack(i).getDamage();
				//System.out.println("Checking Slot:" + currSlotName + " " + currSlotDurability + " for contain: " + bestToolName);
				//System.out.println(currSlotName.toLowerCase() + "|contains|" + " " + bestToolName.toLowerCase());
				if ((currSlotName.toLowerCase().equals(bestToolName.toLowerCase())||currSlotName.toLowerCase().contains(" " + bestToolName.toLowerCase())) && currSlotDurability > 5) {
					rating = rating + 1000; //Tools are still rated by the enchantment system, but if they're the correct tool, they're boosted
				}
			}
				catch(Exception e) {
				//System.out.println("Atleast you tried");
			}
			if (rating > bestRating) {
				bestRating = rating;
				bestRatingSlot = i;
			}
		}

		return new ToolRaterResult(bestRatingSlot, bestRating);
	}
	
	public String searchForTool(Material material, float hardness) {
		if (material == Material.PLANT) {
			return "hoe";
		} else if (material == Material.SOIL || material == Material.SOLID_ORGANIC) {
			return "shovel";
		} else if (material == Material.WOOD || material == Material.NETHER_WOOD) {
			return "axe";
		} else if (material == Material.STONE || material == Material.METAL) {
			return "pickaxe";
		} else if (material == Material.WOOL || material == Material.LEAVES) {
			return "shears";
		}
		else return "hand";
	}
	
	public boolean faceBlock(BlockPos pos) {
		return face(getWorld().getFacingBounds(pos).random(pos, .95));
	}
	
	public void overrideMovement(Input i) {
		if (resetMovementInput == null) {
			resetMovementInput = getClient().player.input;
		}
		getClient().player.input = i;
	}
	
	public void overrideUseItem() {
		if (LOGGER.isDebugEnabled(MARKER_FACING)) {
			HitResult rayTrace = getObjectMouseOver();
			LOGGER.debug(MARKER_FACING, "Using item while facing pos=" +
					(rayTrace instanceof BlockHitResult ? ((BlockHitResult) rayTrace).getPos() : "-")
					+ ", entity=" + (rayTrace instanceof EntityHitResult ? ((EntityHitResult) rayTrace).getEntity() : "-"));
		}
		overrideKey(KeyType.USE);
	}
	
	public void overrideAttack() {
		overrideKey(KeyType.ATTACK);
	}
	
	public void overrideSneak() {
		overrideKey(KeyType.SNEAK);
	}
	
	public void overrideSprint() {
		overrideKey(KeyType.SPRINT);
	}
	
	private void overrideKey(KeyType type) {
		keys.get(type).overridePressed();
	}
	
	public boolean walkTowards(double x, double z, boolean jump, boolean face) {
		final double dx = x - getClient().player.getX();
		final double dz = z - getClient().player.getZ();
		final double distTo = Math.sqrt(dx * dx + dz * dz);
		boolean arrived = distTo > MIN_DISTANCE_ERROR;
		if (arrived) {
			if (face) {
				face(x, getClient().player.getStandingEyeHeight() + getClient().player.getY(), z, 1,
						.1f);
			}
			double speed = 1;
			if (distTo < 4 * WALK_PER_STEP) {
				speed = Math.max(distTo / WALK_PER_STEP / 4, 0.1);
			}
			final double yaw = getClient().player.getYaw() / 180 * Math.PI;
			final double lookX = -Math.sin(yaw);
			final double lookZ = Math.cos(yaw);
			final double dlength = Math.sqrt(dx * dx + dz * dz);
			final double same = (lookX * dx + lookZ * dz) / dlength;
			final double strafe = (lookZ * dx - lookX * dz) / dlength;
			final Input movement = new Input();
			movement.movementForward = (float) (speed * same);
			movement.movementSideways = (float) (speed * strafe);
			movement.jumping = jump;
			overrideMovement(movement);
			if (distTo < 0.5 || getClient().player.isSprinting() && distTo < 0.8) {
				overrideSneak();
			} else if (distTo > 6) {
				overrideSprint();
			}
			return false;
		} else {
			return true;
		}
	}
	
	public double getRequiredAngularChangeTo(double x, double y, double z) {
		final double d0 = x - getClient().player.getX();
		final double d1 = z - getClient().player.getZ();
		final double d2 = y - getClient().player.getY() - getClient().player.getEyeY();
		final double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 < 2.500000277905201E-7D) {
			return 0;
		}
		
		Vec3d playerLook = getClient().player.getRotationVector().normalize();
		return Math.acos(playerLook.dotProduct(new Vec3d(d0, d1, d2).normalize()));
	}
	
	public boolean isFacingBlock(BlockPos pos) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public boolean isFacingBlock(int x, int y, int z) {
		final HitResult position = getObjectMouseOver();
		return position != null
				&& position.getType() == HitResult.Type.BLOCK
				&& new BlockPos(x, y, z).equals(((BlockHitResult)position).getPos())
				&& (y < 255 || allowTopOfWorldHit() || ((BlockHitResult)position).getSide() != Direction.UP);
	}
	
	private boolean allowTopOfWorldHit() {
		return EuphoniumSettings.getSettings().getSaferules().isAllowTopOfWorldHit();
	}

	public boolean isFacingBlock(BlockPos pos, Direction blockSide,
			BlockHalf half) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ(), blockSide,
				half);
	}
	
	public boolean isFacingBlock(int x, int y, int z, Direction blockSide,
			BlockHalf half) {
		if (!isFacingBlock(x, y, z, blockSide)) {
			return false;
		} else {
			final double fy = getObjectMouseOver().getPos().y - y;
			return half != BlockHalf.LOWER_HALF && fy > .5
					|| half != BlockHalf.UPPER_HALF && fy <= .5;
		}
	}
	
	public boolean isFacingBlock(BlockPos pos, Direction side) {
		return isFacingBlock(pos.getX(), pos.getY(), pos.getZ(), side);
	}
	
	public boolean isFacingBlock(int x, int y, int z, Direction side) {
		final HitResult position = getObjectMouseOver();
		return isFacingBlock(x, y, z) && ((BlockHitResult)position).getSide() == side;
	}
	

	public boolean isStandingOn(BlockPos pos) {
		return isStandingOn(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public boolean isFacing(Vec3d vec) {
		return isFacing(vec.x, vec.y, vec.z);
	}
	
	public boolean isFacing(double x, double y, double z) {
		return face(x, y, z, 0, 0);
	}

	
	public boolean face(Vec3d vec) {
		return face(vec.x, vec.y, vec.z);
	}
	
	public boolean face(double x, double y, double z) {
		return face(x, y, z, 1, 1);
	}
	
	private boolean face(double x, double y, double z, float yawInfluence,
			float pitchInfluence) {
		final double d0 = x - getClient().player.getX();
		final double d1 = z - getClient().player.getZ();
		final double d2 = y - getClient().player.getY() - getClient().player.getStandingEyeHeight();
		final double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 >= 2.500000277905201E-7D) {
			final float rotationYaw = getClient().player.getYaw();
			final float rotationPitch = getClient().player.getPitch();

			final float yaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
			final float pitch = (float) -(Math.atan2(d2,
					Math.sqrt(d0 * d0 + d1 * d1)) * 180.0D / Math.PI);
			float yawChange = closestRotation(yaw - rotationYaw);
			float pitchChange = pitch - rotationPitch;
			assert -Math.PI <= yawChange && yawChange <= Math.PI;
			SaferuleSettings saferules = EuphoniumSettings.getSettings().getSaferules();
			float yawClamp = Math.min(Math.abs(saferules.getMaxYawChangeDegrees() / yawChange), 1);
			float pitchClamp = Math.min(
					Math.abs(saferules.getMaxPitchChangeDegrees() / pitchChange), 1);
			float clamp = Math.min(yawClamp, pitchClamp);
			if (yawInfluence <= 0e-5 && pitchInfluence <= 0e-5) {
				// only test, do not set
				return Math.abs(yawChange) < .01 && Math.abs(pitchChange) < .01;
			}

			yawInfluence = Math.min(yawInfluence, clamp);
			pitchInfluence = Math.min(pitchInfluence, clamp);
			// TODO: Make this linear?

			getClient().player.updatePositionAndAngles(
					getClient().player.getX(),
					getClient().player.getY(),
					getClient().player.getZ(),
					rotationYaw + yawChange * yawInfluence,
					rotationPitch + pitchChange * pitchInfluence);
			invalidateObjectMouseOver();

			return clamp > .999;
		}
		return true;
	}
	
	public boolean selectCurrentItem(ItemFilter f) {
		if (f.matches(getClient().player.getInventory().getMainHandStack())) {
			return true;
		}
		for (int i = 0; i < 9; ++i) {
			if (f.matches(getClient().player.getInventory().getStack(i))) {
				getClient().player.getInventory().selectedSlot = i;
				return true;
			}
		}
		return false;
	}
	
	public boolean arrivedAt(double x, double z) {
		final double dx = x - getClient().player.getX();
		final double dz = z - getClient().player.getZ();
		final double distTo = Math.sqrt(dx * dx + dz * dz);
		return distTo <= MIN_DISTANCE_ERROR;
	}

	public boolean isJumping() {
		// func_233570_aj_ -> onGround
		return !getClient().player.isOnGround();
	}
	
	public static Direction getDirectionFor(BlockPos delta) {
		for (final Direction d : Direction.values()) {
			if (Pos.fromDir(d).equals(delta)) {
				return d;
			}
		}
		throw new IllegalArgumentException("Cannot convert to direction: "
				+ delta);
	}
	
	public StatsManager getStats() {
		return stats;
	}
}
