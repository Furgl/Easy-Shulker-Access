package furgl.shulkerBox;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;

public class TileEntityShulkerAccessBox extends TileEntityShulkerBox {

	public TileEntityShulkerAccessBox(ItemStack stack) {
		if (stack != null && stack.hasTagCompound())
			this.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

}