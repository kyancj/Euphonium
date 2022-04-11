package net.cancheta.ai.task.inventory;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemWithSubtype {

	private final int itemId;

	public ItemWithSubtype(ItemStack stack) {
		this(Item.getRawId(stack.getItem()));
	}

	public ItemWithSubtype(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemWithSubtype that = (ItemWithSubtype) o;
		return itemId == that.itemId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId);
	}

	@Override
	public String toString() {
		return itemId + "";
	}

	public Item getItem() {
		return Item.byRawId(itemId);
	}
	
	public Block getBlockType() {
		Item item = getItem();
		if (item instanceof BlockItem) {
			BlockItem itemBlock = (BlockItem) item;
			return itemBlock.getBlock();
		} else {
			return null;
		}
	}

	public ItemStack getFakeMCStack(int size) {
		Item item = getItem();
		if (item == null) {
			throw new NullPointerException("Could not find item " + itemId);
		}
		ItemStack stack = new ItemStack(item);
		stack.setCount(size);
		return stack;
	}
	
	public static ItemWithSubtype fromStack(ItemStack stack) {
		return stack == null || stack.isEmpty() ? null : new ItemWithSubtype(stack);
	}
	
	/**
	 * Convert a name to an item id. Always sets the subtype to 0.
	 * @param name
	 * @return
	 */
	public static ItemWithSubtype fromTypeName(String name) {
		Item item = Registry.ITEM.get(new Identifier(name));
		return new ItemWithSubtype(Item.getRawId(item));
	}
}
