package furgl.containers;

import furgl.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;

public class ContainerSAPlayer extends PlayerContainer {

	public ContainerSAPlayer(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn) {
		super(playerInventory, localWorld, playerIn);
	}
	
	@Override
	public boolean getCanCraft(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity && slotId >= 0 && slotId < this.inventorySlots.size()) {
			// check if opening shulker box
			if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (ServerPlayerEntity) player, this.getSlot(slotId).getStack()))
				return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}