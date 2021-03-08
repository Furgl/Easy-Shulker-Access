package furgl.storage;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import furgl.CloudStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class InfiniteStackHandler extends ItemStackHandler {

	public InfiniteStackHandler() {
		// have to use this to create the list so it can be resized
		this.stacks = NonNullList.create();
		// add an empty slot by default so we always have size + 1 slots
		for (int i=0;i<36;++i)
			this.stacks.add(ItemStack.EMPTY);
	}

	/**Get all items*/
	public NonNullList<ItemStack> getStacks() {
		return this.stacks;
	}

	@Override
	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Integer.MAX_VALUE;
	}

	@Override
	protected void onContentsChanged(int slot) {
		// TODO sync to client

		CloudStorage.logger.info("Changed storage to: "+this.toString());// TODO remove
	}

	/**Get number of items + 1 (so it will always expand)*/
	@Override
	public int getSlots() {
		return Math.max(36, stacks.size()+1);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);

		// added - if this is last slot (stack size + 1), add another slot
		if (slot == this.stacks.size())
			this.stacks.add(ItemStack.EMPTY);

		this.stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.getSlots())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	/**Get stack in slot or empty item if not valid slot (bc we return stack size + 1 for slots)*/
	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return (slot >= 0 && slot < this.stacks.size()) ? this.stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		// added - if this is last slot (stack size + 1), add another slot
		if (slot == this.stacks.size())
			this.stacks.add(ItemStack.EMPTY);

		ItemStack existing = this.stacks.get(slot);

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty())
			{
				this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			}
			else
			{
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			onContentsChanged(slot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
	}


	@Override
	public String toString() {
		ArrayList<ItemStack> items = Lists.newArrayList();
		for (int i=0; i<this.getSlots(); ++i)
			items.add(this.getStackInSlot(i));
		return items.toString();
	}

}