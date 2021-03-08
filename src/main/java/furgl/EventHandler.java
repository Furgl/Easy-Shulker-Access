package furgl;

import furgl.gui.ContainerCloudPlayer;
import furgl.gui.GuiCloudInventory;
import furgl.gui.InventoryCloudPlayer;
import furgl.storage.StorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
	
	// TODO render big stack sizes better

	/**Attach storage capability to players*/
	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) 
			event.addCapability(StorageManager.Provider.NAME, new StorageManager.Provider());
	}

	/**Copy storage after death*/
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event) {
		event.getEntityPlayer().getCapability(StorageManager.CAPABILITY, null).deserializeNBT(event.getOriginal().getCapability(StorageManager.CAPABILITY, null).serializeNBT());
	}


	/**Add picked up items to storage*/
	@SubscribeEvent 
	public static void pickupItem(EntityItemPickupEvent event) { 
		if (event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote) {
			// TODO add to cloud storage
		}
	}

	/**Open custom gui*/ // TODO move to client side
	@SubscribeEvent
	public static void openGui(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiInventory)
			event.setGui(new GuiCloudInventory(Minecraft.getMinecraft().player)); 
	}

	/**replace player's main inventory*/
	@SubscribeEvent
	public static void onJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			// replace inventory with custom inventory for infinite stacks
			player.inventory = new InventoryCloudPlayer(player);
			player.inventoryContainer = new ContainerCloudPlayer(player.inventory, !player.world.isRemote, player);
			player.openContainer = player.inventoryContainer;
		}
	}

}