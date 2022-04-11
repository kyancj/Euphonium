package net.cancheta.ai.path.world;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockFloatMap {
	private float[] floats = new float[] {Float.NaN, Float.NaN, Float.NaN, Float.NaN};
	private float defaultValue = Float.NaN;

	public BlockFloatMap() {
	}

	public void setDefault(float defaultValue) {
		if (!Float.isNaN(this.defaultValue)) {
			throw new IllegalStateException("Default already set.");
		}

		if (Float.isNaN(defaultValue)) {
			throw new IllegalArgumentException("Default is NaN.");
		}
		this.defaultValue = defaultValue;
	}

	public float get(BlockState blockAndMeta) {
		return get(BlockSet.getStateId(blockAndMeta));
	}

	public float get(int blockAndMeta) {
		if (blockAndMeta < floats.length) {
			float value = floats[blockAndMeta];
			return Float.isNaN(value) ? getDefaultValue() : value;
		} else {
			return getDefaultValue();
		}
	}

	public float getMax() {
		float max = defaultValue;
		for (float f : floats) {
			if (!Float.isNaN(f) && (f > max || Float.isNaN(max))) {
				max = f;
			}
		}
		return max;
	}

	public void setBlock(Block block, float value) {
		block.getStateManager().getStates().forEach(state -> set(state, value));
	}

	public void set(BlockState blockState, float value) {
		int stateId = BlockSet.getStateId(blockState);
		if (floats.length <= stateId) {
			float[] newFloats = Arrays.copyOf(floats, Math.max(stateId + 1, floats.length * 2));
			Arrays.fill(newFloats, floats.length, newFloats.length, Float.NaN);
			this.floats = newFloats;
		}
		floats[stateId] = value;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return "BlockFloatMap [floats=..., defaultValue=" + defaultValue + "]";
	}

	public BlockSet getUsedBlocks() {
		BlockSet.Builder builder = BlockSet.builder();
		for (int i = 0; i < floats.length; i++) {
			if (!Float.isNaN(floats[i])) {
				// Go through id -> state map to validate.
				builder.add(BlockSet.getStateById(i));
			}
		}
		return builder.build();
	}
}
