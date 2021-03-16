package furgl.containers;

import furgl.shulkerBox.TileEntityShulkerAccessBox;
import furgl.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class ContainerSAShulkerBox extends ShulkerBoxContainer {

	private ItemStack stack;

	public ContainerSAShulkerBox(int windowId, PlayerInventory inventory, TileEntityShulkerAccessBox te, ItemStack stack) {
		super(windowId, inventory, te);
		this.stack = stack;
		Utils.updateTooltip(stack, false);
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		Utils.updateTooltip(stack, true);
		// update to client cuz the container changes so it won't update on its own
		if (playerIn instanceof ServerPlayerEntity) {
			for (int i=0; i<this.inventorySlots.size(); ++i) {
				if (ItemStack.areItemStacksEqual(stack, this.inventorySlots.get(i).getStack())) 
					// slot-18 for some reason..
					((ServerPlayerEntity) playerIn).connection.sendPacket(new SSetSlotPacket(playerIn.container.windowId, i-18, stack.copy()));
			}
		}
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean getCanCraft(PlayerEntity player) {
		return true;
	}

	@Override 
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity && slotId >= 0 && slotId < this.inventorySlots.size()) {
			// prevent interacting with this shulker box
			if (ItemStack.areItemStacksEqual(stack, this.getSlot(slotId).getStack()) && stack != null && !stack.isEmpty()) {
				// close screen if this shulker box right-clicked (no way to open inventory from server, so just close everything)
				if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {
					Utils.updateTooltip(stack, true);
					((ServerPlayerEntity) player).closeScreen();
					((ServerPlayerEntity) player).connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_SHULKER_BOX_CLOSE.getRegistryName(), SoundCategory.BLOCKS, player.getPositionVec(), 0.5f, 1.0f));
				}
				return ItemStack.EMPTY;
			}
			// check if opening shulker box
			else if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (ServerPlayerEntity) player, this.getSlot(slotId).getStack()))
				return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}