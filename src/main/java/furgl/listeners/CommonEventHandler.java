package furgl.listeners;

import furgl.containers.ContainerSAPlayer;
import furgl.utils.Utils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler {

	/**Replace player's main inventory*/	
	@SubscribeEvent
	public static void onJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			player.container = new ContainerSAPlayer(player.inventory, !player.world.isRemote, player);
			player.openContainer = player.container;
		}
	}
	
	/**Add tooltip when crafting shulker boxes*/
	@SubscribeEvent
	public static void onShulkerCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.getCrafting() != null && event.getCrafting().getItem() instanceof BlockItem &&
				((BlockItem)event.getCrafting().getItem()).getBlock() instanceof ShulkerBoxBlock) 
			Utils.updateTooltip(event.getCrafting(), true);
	}
	
}