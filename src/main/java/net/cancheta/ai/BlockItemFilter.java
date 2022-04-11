package net.cancheta.ai;

import net.cancheta.ai.path.world.BlockSet;
import net.cancheta.ai.task.inventory.ItemWithSubtype;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class BlockItemFilter implements HumanReadableItemFilter {

	private final BlockSet matched;

	public BlockItemFilter(Block... matched) {
		this(BlockSet.builder().add(matched).build());
	}

	public BlockItemFilter(BlockState... matched) {
		this(BlockSet.builder().add(matched).build());
	}

	public BlockItemFilter(BlockSet matched) {
		this.matched = matched;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() != null
				&& matchesItem(itemStack);
	}

	protected boolean matchesItem(ItemStack itemStack) {
		Block blockType = new ItemWithSubtype(itemStack).getBlockType();
		return blockType != null && matched.contains(blockType.getDefaultState());
	}

	public BlockSet getMatched() {
		return matched;
	}

	@Override
	public String toString() {
		return "BlockItemFilter [matched=" + matched + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matched == null) ? 0 : matched.hashCode());
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
		BlockItemFilter other = (BlockItemFilter) obj;
		if (matched == null) {
			if (other.matched != null)
				return false;
		} else if (!matched.equals(other.matched))
			return false;
		return true;
	}

	@Override
	public String getDescription() {
		return matched.getBlockString();
	}
}
