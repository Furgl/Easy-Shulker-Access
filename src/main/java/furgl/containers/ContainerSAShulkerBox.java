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
	public void removed(PlayerEntity playerIn) {
		Utils.updateTooltip(stack, true);
		// update to client cuz the container changes so it won't update on its own
		if (playerIn instanceof ServerPlayerEntity) {
			for (int i=0; i<this.slots.size(); ++i) {
				if (ItemStack.matches(stack, this.slots.get(i).getItem())) 
					// slot-18 for some reason..
					((ServerPlayerEntity) playerIn).connection.send(new SSetSlotPacket(playerIn.containerMenu.containerId, i-18, stack.copy()));
			}
		}
		super.removed(playerIn);
	}

	@Override
	public boolean isSynched(PlayerEntity player) {
		return true;
	}

	@Override 
	public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity && slotId >= 0 && slotId < this.slots.size()) {
			// prevent interacting with this shulker box
			if (ItemStack.matches(stack, this.getSlot(slotId).getItem()) && stack != null && !stack.isEmpty()) {
				// close screen if this shulker box right-clicked (no way to open inventory from server, so just close everything)
				if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {
					Utils.updateTooltip(stack, true);
					((ServerPlayerEntity) player).closeContainer();
					((ServerPlayerEntity) player).connection.send(new SPlaySoundPacket(SoundEvents.SHULKER_BOX_CLOSE.getRegistryName(), SoundCategory.BLOCKS, player.position(), 0.5f, 1.0f));
				}
				return ItemStack.EMPTY;
			}
			// check if opening shulker box
			else if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (ServerPlayerEntity) player, this.getSlot(slotId).getItem()))
				return ItemStack.EMPTY;
		}

		return super.clicked(slotId, dragType, clickTypeIn, player);
	}

}