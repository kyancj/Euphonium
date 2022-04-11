package net.cancheta.ai;

import net.minecraft.item.ItemStack;

public interface ItemFilter {
	ItemFilter ANY = new ItemFilter(){
		@Override
		public boolean matches(ItemStack itemStack) {
			return true;
		}
	};

	public static class OrItemFilter implements ItemFilter {
		
		private final ItemFilter[] filters;

		public OrItemFilter(ItemFilter...filters) {
			this.filters = filters;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			for (ItemFilter f : filters) {
				if (f.matches(itemStack)) {
					return true;
				}
			}
			return false;
		}
		
		public ItemFilter[] getFilters() {
			return filters;
		}
	}

	public static class AndItemFilter implements ItemFilter {
		
		private final ItemFilter[] filters;

		public AndItemFilter(ItemFilter...filters) {
			this.filters = filters;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			for (ItemFilter f : filters) {
				if (!f.matches(itemStack)) {
					return false;
				}
			}
			return true;
		}
		
		public ItemFilter[] getFilters() {
			return filters;
		}
	}

	public static class NotItemFilter implements ItemFilter {
		
		private final ItemFilter filter;

		public NotItemFilter(ItemFilter filter) {
			this.filter = filter;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			return !filter.matches(itemStack);
		}
		public ItemFilter getFilter() {
			return filter;
		}
	}
	
	/**
	 * Checks if this filter matches the item.
	 * 
	 * @param itemStack
	 *            The item stack. It might be <code>null</code> for an empty
	 *            stack
	 * @return If the stack matches this filter.
	 */
	boolean matches(ItemStack itemStack);
}
