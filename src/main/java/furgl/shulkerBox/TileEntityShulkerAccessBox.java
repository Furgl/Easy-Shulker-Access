package furgl.shulkerBox;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;

public class TileEntityShulkerAccessBox extends ShulkerBoxTileEntity {

	public TileEntityShulkerAccessBox(ItemStack stack) {
		if (stack != null && stack.hasTag())
			this.read(stack.getTag().getCompound("BlockEntityTag"));
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

}