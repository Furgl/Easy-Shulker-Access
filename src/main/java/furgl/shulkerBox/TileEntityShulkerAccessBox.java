package furgl.shulkerBox;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;

public class TileEntityShulkerAccessBox extends ShulkerBoxTileEntity {

	public TileEntityShulkerAccessBox(ItemStack stack) {
		if (stack != null && stack.hasTag())
			this.loadFromTag(stack.getTag().getCompound("BlockEntityTag"));
	}

	@Override
	public boolean canOpen(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean stillValid(PlayerEntity p_70300_1_) {
		return true;
	}

}