package furgl.containers;

import furgl.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSAPlayer extends ContainerPlayer {

	public ContainerSAPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn) {
		super(playerInventory, localWorld, playerIn);
	}
	
	@Override
	public boolean getCanCraft(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (player instanceof EntityPlayerMP && slotId >= 0 && slotId < this.inventorySlots.size()) {
			// check if opening shulker box
			if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (EntityPlayerMP) player, this.getSlot(slotId).getStack()))
				return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}