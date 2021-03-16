package furgl.shulkerBox;

import furgl.containers.ContainerSAShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;

public class TileEntityShulkerAccessBox extends TileEntityShulkerBox {

	private ItemStack stack;

	public TileEntityShulkerAccessBox(ItemStack stack) {
		if (stack != null) {
			if (stack.hasTagCompound())
				this.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			if (stack.hasDisplayName())
				this.setCustomName(stack.getDisplayName());
		}
		this.stack = stack;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	 public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerSAShulkerBox(playerInventory, this, playerIn, this.stack);
	}
	
}