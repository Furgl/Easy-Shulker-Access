package furgl.shulkerBox;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ShulkerBoxListener implements IContainerListener {

	private ServerPlayerEntity player;
	private ItemStack stack;

	public ShulkerBoxListener(ServerPlayerEntity player, ItemStack stack) {
		this.player = player;
		this.stack = stack;
	}

	@Override
	public void refreshContainer(Container container, NonNullList<ItemStack> items) {
		this.updateStack(container, items);
	}

	@Override
	public void slotChanged(Container container, int slot, ItemStack stack) {
		this.updateStack(container, container.getItems());
	}

	/**Update the shulker box stack with the contents of the shulker box*/
	public void updateStack(Container container, NonNullList<ItemStack> items) {
		if (this.stack != null) {
			// ignore the last 36 items (bc they're the player's inventory)
			for (int i=items.size()-36; i>=0 && i<items.size(); ++i)
				items.set(i, ItemStack.EMPTY);
			CompoundNBT nbt = this.stack.hasTag() ? this.stack.getTag() : new CompoundNBT();
			CompoundNBT blockEntityTag = nbt.getCompound("BlockEntityTag");
			ItemStackHelper.saveAllItems(blockEntityTag, items, true);
			nbt.put("BlockEntityTag", blockEntityTag);
			this.stack.setTag(nbt);
			// update it in inv (otherwise tooltip doesn't seem to update)
			this.player.refreshContainer(container);
			// if player doesn't have shulker anymore somehow (i.e. throwing it), close it
			if (!this.player.inventory.contains(this.stack))
				this.player.closeContainer();
		}
	}

	@Override
	public void setContainerData(Container containerIn, int varToUpdate, int newValue) {}

}