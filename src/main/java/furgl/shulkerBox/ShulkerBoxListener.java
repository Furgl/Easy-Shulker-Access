package furgl.shulkerBox;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class ShulkerBoxListener implements IContainerListener {

	private EntityPlayerMP player;
	private ItemStack stack;

	public ShulkerBoxListener(EntityPlayerMP player, ItemStack stack) {
		this.player = player;
		this.stack = stack;
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> items) {
		this.updateStack(container, items);
	}

	@Override
	public void sendSlotContents(Container container, int slot, ItemStack stack) {
		this.updateStack(container, container.getInventory());
	}

	/**Update the shulker box stack with the contents of the shulker box*/
	public void updateStack(Container container, NonNullList<ItemStack> items) {
		if (this.stack != null) {
			// ignore the last 36 items (bc they're the player's inventory)
			for (int i=items.size()-36; i>=0 && i<items.size(); ++i)
				items.set(i, ItemStack.EMPTY);
			NBTTagCompound nbt = this.stack.hasTagCompound() ? this.stack.getTagCompound() : new NBTTagCompound();
			NBTTagCompound blockEntityTag = nbt.getCompoundTag("BlockEntityTag");
			ItemStackHelper.saveAllItems(blockEntityTag, items, true);
			nbt.setTag("BlockEntityTag", blockEntityTag);
			this.stack.setTagCompound(nbt);
			// update it in inv (otherwise tooltip doesn't seem to update)
			this.player.sendContainerToPlayer(container);
			// if player doesn't have shulker anymore somehow (i.e. throwing it), close it
			if (!this.player.inventory.hasItemStack(this.stack))
				this.player.closeScreen();
		}
	}

	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {}

}