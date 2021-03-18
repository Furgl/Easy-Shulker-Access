package furgl.containers;

import cpw.mods.ironchest.common.blocks.shulker.IronShulkerBoxType;
import cpw.mods.ironchest.common.gui.shulker.ContainerIronShulkerBox;
import furgl.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.SoundCategory;

public class ContainerSAIronShulkerBox extends ContainerIronShulkerBox {

	private ItemStack stack;

	public ContainerSAIronShulkerBox(InventoryPlayer playerInventoryIn, IInventory inventoryIn, ItemStack stack, IronShulkerBoxType type) {
		super(playerInventoryIn, inventoryIn, type, type.xSize, type.ySize);
		this.stack = stack;
		Utils.updateTooltip(stack, false);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		Utils.updateTooltip(stack, true);
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean getCanCraft(EntityPlayer player) {
		return true;
	}

	@Override 
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (player instanceof EntityPlayerMP && slotId >= 0 && slotId < this.inventorySlots.size()) {
			// prevent interacting with this shulker box
			if (ItemStack.areItemStacksEqual(stack, this.getSlot(slotId).getStack()) && stack != null && !stack.isEmpty()) {
				// close screen if this shulker box right-clicked (no way to open inventory from server, so just close everything)
				if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {
					Utils.updateTooltip(stack, true);
					((EntityPlayerMP) player).closeScreen();
					((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound(SoundEvents.BLOCK_SHULKER_BOX_CLOSE.getRegistryName().toString(), SoundCategory.BLOCKS, player.posX, player.posY, player.posZ, 0.5f, 1.0f));
				}
				return ItemStack.EMPTY;
			}
			// check if opening shulker box
			else if (Utils.tryOpeningShulkerBox(slotId, dragType, clickTypeIn, (EntityPlayerMP) player, this.getSlot(slotId).getStack()))
				return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}