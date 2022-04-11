package net.cancheta.ai.tools;

import net.cancheta.ai.path.world.BlockFloatMap;
import net.minecraft.item.ItemStack;

public abstract class Rater {

	protected final String name;

	protected final BlockFloatMap values;

	public Rater(String name, BlockFloatMap values) {
		super();
		this.name = name;
		this.values = values;
	}
	
	public float rate(ItemStack item, int forBlockAndMeta) {
		if (isApplicable(item, forBlockAndMeta)) {
			return forBlockAndMeta < 0 ? values.getDefaultValue() : values.get(forBlockAndMeta);
		} else {
			return 1;
		}
	}
	
	protected boolean isApplicable(ItemStack item, int forBlockAndMeta) {
		return true;
	}

}
