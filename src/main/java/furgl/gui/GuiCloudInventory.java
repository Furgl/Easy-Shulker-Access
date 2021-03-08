package furgl.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiCloudInventory extends GuiInventory {

	// TODO dragging item stack sizes (holding left click and holding middle click)
	public GuiCloudInventory(EntityPlayer player) {
		super(player);
		this.inventorySlots = player.inventoryContainer;
	}
	
}