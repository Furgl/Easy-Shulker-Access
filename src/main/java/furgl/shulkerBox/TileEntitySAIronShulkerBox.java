package furgl.shulkerBox;

import cpw.mods.ironchest.common.blocks.shulker.IronShulkerBoxType;
import cpw.mods.ironchest.common.tileentity.shulker.TileEntityIronShulkerBox;
import furgl.containers.ContainerSAIronShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntitySAIronShulkerBox extends TileEntityIronShulkerBox {

	private ItemStack stack;
	private IronShulkerBoxType type;

	public TileEntitySAIronShulkerBox(World world, ItemStack stack, IronShulkerBoxType type) {
		super(null, type);
		this.setWorld(world);
		this.type = type;
		this.stack = stack;
		if (stack != null) {
			if (stack.hasTagCompound())
				this.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			this.setCustomName(stack.getDisplayName());
		}
	}
	
	@Override
	 public IronShulkerBoxType getType() {
		return this.type;
	}
	
	@Override
	protected void sendTopStacksPacket() {}
	
	@Override
	public int getSizeInventory() {
		return this.type.size;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerSAIronShulkerBox(playerInventory, this, this.stack, type);
	}

}