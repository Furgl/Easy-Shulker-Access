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
	public boolean isSynched(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity && slotId >= 0 && slotId < this.slots.size()) {
			// check if opening shulker box
			if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (ServerPlayerEntity) player, this.getSlot(slotId).getItem()))
				return ItemStack.EMPTY;
		}

		return super.clicked(slotId, dragType, clickTypeIn, player);
	}

}