package furgl.listeners;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import furgl.ShulkerAccess;
import furgl.packets.CPacketOpenShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {

	/**Slot in player's inventory of the shulker box that is opened*/
	private static int selectedShulkerBoxSlot = -1;

	/**Reset selected shulker box slot when container is closed*/ 
	@SubscribeEvent
	public static void closeContainer(GuiOpenEvent event) {
		if (!(event.getGui() instanceof GuiShulkerBox)) 
			selectedShulkerBoxSlot = -1;
	}

	/**Add info to tooltips*/ 
	@SubscribeEvent
	public static void tooltipEvent(ItemTooltipEvent event) {
		try {
			if (event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemShulkerBox &&
					Minecraft.getMinecraft().player != null) {
				Slot slot = selectedShulkerBoxSlot == -1 ? null : Minecraft.getMinecraft().player.inventoryContainer.getSlotFromInventory(Minecraft.getMinecraft().player.inventory, selectedShulkerBoxSlot);
				if (slot != null && slot.getStack() == event.getItemStack())
					event.getToolTip().add(TextFormatting.RED+""+TextFormatting.ITALIC+"Right-click to close");
				else
					event.getToolTip().add(TextFormatting.GOLD+""+TextFormatting.ITALIC+"Right-click to open");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Highlight selected shulker box slot*/
	@SubscribeEvent
	public static void drawGui(GuiContainerEvent.DrawForeground event) {
		try {
			if (event.getGuiContainer()instanceof GuiShulkerBox && 
					selectedShulkerBoxSlot != -1) {
				Slot slot = Minecraft.getMinecraft().player.inventoryContainer.getSlotFromInventory(Minecraft.getMinecraft().player.inventory, selectedShulkerBoxSlot);
				ItemStack stack = slot.getStack();
				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemShulkerBox) {
					GlStateManager.pushMatrix();
					Gui.drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, new Color(47, 98, 255).getRGB());
					GlStateManager.popMatrix();
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void onClickPre(GuiScreenEvent.MouseInputEvent.Pre event) {
		try {
			if (event.getGui() instanceof GuiContainer) {
				GuiContainer container = ((GuiContainer)event.getGui());
				// clicking on a slot in player's inventory
				if (container.getSlotUnderMouse() != null && 
						container.getSlotUnderMouse().inventory instanceof InventoryPlayer) {
					// clicking shulker box
					if (container.getSlotUnderMouse().getStack() != null && 
							container.getSlotUnderMouse().getStack().getItem() instanceof ItemShulkerBox) {
						// already selected
						if (selectedShulkerBoxSlot == container.getSlotUnderMouse().getSlotIndex() &&
								container instanceof GuiShulkerBox) {
							// if right-clicking, deselect it, otherwise ignore click
							if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
								selectedShulkerBoxSlot = -1;
								Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().player));
								ShulkerAccess.network.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
								Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_SHULKER_BOX_CLOSE, 0.5f, 1.0f);
							}
							event.setCanceled(true);
						}
						// select and open shulker box
						else if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
							selectedShulkerBoxSlot = container.getSlotUnderMouse().getSlotIndex();
							ShulkerAccess.network.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
							Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN, 0.5f, 1.0f);
							event.setCanceled(true);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}