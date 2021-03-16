package furgl.shulkerBox;

import furgl.containers.ContainerSAShulkerBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;

public class TileEntityShulkerAccessBox extends ShulkerBoxTileEntity {

	private ItemStack stack;

	public TileEntityShulkerAccessBox(ItemStack stack) {
		if (stack != null) {
			if (stack.hasTag())
				this.loadFromTag(stack.getTag().getCompound("BlockEntityTag"));
			if (stack.hasCustomHoverName())
				this.setCustomName(stack.getHoverName());
		}
		this.stack = stack;
	}

	@Override
	public boolean canOpen(PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public Container createMenu(int id, PlayerInventory inventory) {
		return new ContainerSAShulkerBox(id, inventory, this, this.stack);
	}
	
}