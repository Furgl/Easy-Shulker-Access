package furgl.listeners;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import furgl.ShulkerAccess;
import furgl.packets.CPacketOpenShulkerBox;
import furgl.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
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

	/**Reset selected shulker box slot null container is opened (no container is opened)*/ 
	@SubscribeEvent
	public static void openGui(GuiOpenEvent event) {
		if (event.getGui() == null) 
			selectedShulkerBoxSlot = -1;
	}

	/** Add info to tooltips 
	 * Fixes any issues with open/close being wrong (mainly for creative mode)*/
	@SubscribeEvent
	public static void tooltipEvent(ItemTooltipEvent event) {
		try { 
			if (event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemShulkerBox && 
					Minecraft.getMinecraft().player != null) {
				if (selectedShulkerBoxSlot != -1 && event.getEntityPlayer().inventory.getStackInSlot(selectedShulkerBoxSlot) == event.getItemStack())
					event.getToolTip().replaceAll(str -> {
						if (str.contains(Utils.OPEN_TEXT))
							return Utils.CLOSE_TEXT;
						else
							return str;
					});

				else
					event.getToolTip().replaceAll(str -> {
						if (str.contains(Utils.CLOSE_TEXT))
							return Utils.OPEN_TEXT;
						else
							return str;
					});
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
			if (event.getGuiContainer() instanceof GuiShulkerBox && selectedShulkerBoxSlot != -1) {
				ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(selectedShulkerBoxSlot);
				Slot slot = null;
				for (Slot s : Minecraft.getMinecraft().player.inventoryContainer.inventorySlots)
					if (s != null && s.getStack() == stack) {
						slot = s;
						break;
					}
				if (slot != null) {
					GlStateManager.pushMatrix();
					Gui.drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, new Color(47, 98, 255).getRGB());
					GlStateManager.popMatrix();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void onClickPre(GuiScreenEvent.MouseInputEvent.Pre event) {
		try {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player != null && event.getGui() instanceof GuiContainer) {
				GuiContainer container = ((GuiContainer)event.getGui());
				// clicking on a slot in player's inventory
				if (container.getSlotUnderMouse() != null && 
						container.getSlotUnderMouse().inventory instanceof InventoryPlayer) {
					// clicking shulker box
					if (container.getSlotUnderMouse().getStack() != null && 
							container.getSlotUnderMouse().getStack().getItem() instanceof ItemShulkerBox) {
						// already selected
						if (selectedShulkerBoxSlot == container.getSlotUnderMouse().getSlotIndex()
								&& container instanceof GuiShulkerBox) {
							// if right-clicking, deselect it, otherwise ignore click
							if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
								selectedShulkerBoxSlot = -1;
								ShulkerAccess.network.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
								Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(player));
							}
							event.setCanceled(true);
						}
						// select and open shulker box
						else if (selectedShulkerBoxSlot != container.getSlotUnderMouse().getSlotIndex() &&
								Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
							selectedShulkerBoxSlot = container.getSlotUnderMouse().getSlotIndex();
							ShulkerAccess.network.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
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