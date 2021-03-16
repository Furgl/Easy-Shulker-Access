package furgl.listeners;

import furgl.containers.ContainerSAPlayer;
import furgl.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class CommonEventHandler {

	/**Replace player's main inventory*/	
	@SubscribeEvent
	public static void onJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			player.inventoryContainer = new ContainerSAPlayer(player.inventory, !player.world.isRemote, player);
			player.openContainer = player.inventoryContainer;
		}
	}
	
	/**Add tooltip when crafting shulker boxes*/
	@SubscribeEvent
	public static void onShulkerCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting != null && event.crafting.getItem() instanceof ItemShulkerBox) 
			Utils.updateTooltip(event.crafting, true);
	}
	
}