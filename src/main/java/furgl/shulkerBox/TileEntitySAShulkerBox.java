package furgl.shulkerBox;

import furgl.containers.ContainerSAShulkerBox;
import furgl.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.world.World;

public class TileEntitySAShulkerBox extends TileEntityShulkerBox {

	private ItemStack stack;

	public TileEntitySAShulkerBox(World world, ItemStack stack) {
		super(Utils.getColor(stack));
		this.setWorld(world);
		this.stack = stack;
		if (stack != null) {
			if (stack.hasTagCompound())
				this.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			if (stack.hasDisplayName())
				this.setCustomName(stack.getDisplayName());
		}
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