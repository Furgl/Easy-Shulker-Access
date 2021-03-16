package furgl.listeners;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import furgl.ShulkerAccess;
import furgl.packets.CPacketOpenShulkerBox;
import furgl.utils.Utils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
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
			if (event.getItemStack() != null && event.getItemStack().getItem() instanceof BlockItem &&
					((BlockItem)event.getItemStack().getItem()).getBlock() instanceof ShulkerBoxBlock && 
					Minecraft.getInstance().player != null) {
				if (selectedShulkerBoxSlot != -1 && event.getPlayer().inventory.getItem(selectedShulkerBoxSlot) == event.getItemStack())
					event.getToolTip().replaceAll(comp -> {
						if (comp.getString().contains(Utils.OPEN_TEXT))
							return new StringTextComponent(Utils.CLOSE_TEXT);
						else
							return comp;
					});

				else
					event.getToolTip().replaceAll(comp -> {
						if (comp.getString().contains(Utils.CLOSE_TEXT))
							return new StringTextComponent(Utils.OPEN_TEXT);
						else
							return comp;
					});
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Highlight selected shulker box slot*/
	@SubscribeEvent
	public static void drawGui(GuiContainerEvent.DrawBackground event) {
		try {
			if (event.getGuiContainer() instanceof ShulkerBoxScreen && selectedShulkerBoxSlot != -1) {
				ItemStack stack = Minecraft.getInstance().player.inventory.getItem(selectedShulkerBoxSlot);
				Slot slot = null;
				for (Slot s : Minecraft.getInstance().player.containerMenu.slots)
					if (s != null && s.getItem() == stack) {
						slot = s;
						break;
					}
				if (slot != null) {
					RenderSystem.disableRescaleNormal();
					RenderSystem.disableDepthTest();
					RenderSystem.pushMatrix();
					RenderSystem.translatef(event.getGuiContainer().getGuiLeft(), event.getGuiContainer().getGuiTop(), 0.0F);
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.enableRescaleNormal();
					RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					Screen.fill(event.getMatrixStack(), slot.x, slot.y, slot.x + 16, slot.y + 16, new Color(47, 98, 255).getRGB());
					RenderSystem.popMatrix();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void onClickPre(GuiScreenEvent.MouseClickedEvent.Pre event) {
		try {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player != null && event.getGui() instanceof ContainerScreen) {
				ContainerScreen container = ((ContainerScreen)event.getGui());
				// clicking on a slot in player's inventory
				if (container.getSlotUnderMouse() != null && 
						container.getSlotUnderMouse().container instanceof PlayerInventory) {
					// clicking shulker box
					if (container.getSlotUnderMouse().getItem() != null && 
							container.getSlotUnderMouse().getItem().getItem() instanceof BlockItem &&
							((BlockItem)container.getSlotUnderMouse().getItem().getItem()).getBlock() instanceof ShulkerBoxBlock) {
						// already selected
						if (selectedShulkerBoxSlot == container.getSlotUnderMouse().getSlotIndex()
								&& container instanceof ShulkerBoxScreen) {
							// if right-clicking, deselect it, otherwise ignore click
							if (event.getButton() == 1) {
								selectedShulkerBoxSlot = -1;
								ShulkerAccess.NETWORK.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
								Minecraft.getInstance().setScreen(new InventoryScreen(player));
							}
							event.setCanceled(true);
						}
						// select and open shulker box
						else if (selectedShulkerBoxSlot != container.getSlotUnderMouse().getSlotIndex() &&
								event.getButton() == 1) {
							selectedShulkerBoxSlot = container.getSlotUnderMouse().getSlotIndex();
							ShulkerAccess.NETWORK.sendToServer(new CPacketOpenShulkerBox(selectedShulkerBoxSlot));
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