package furgl.listeners;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.Lists;

import furgl.containers.ContainerSAPlayer;
import furgl.utils.Utils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler {

	/**Newly created players that need to have their inventories replaced*/
	private static ArrayList<PlayerEntity> newPlayers = Lists.newArrayList();

	@SubscribeEvent
	public static void onPlayerCreated(EntityEvent.EntityConstructing event) {
		if (event.getEntity() instanceof PlayerEntity && !event.getEntity().level.isClientSide) 
			newPlayers.add((PlayerEntity)event.getEntity());
	}

	@SubscribeEvent
	public static void onTick(ServerTickEvent event) {
		if (!newPlayers.isEmpty()) {
			Iterator<PlayerEntity> it = newPlayers.iterator();
			while (it.hasNext()) {
				PlayerEntity player = it.next();
				if (player.tickCount > 5)
					replaceInventory(player);
				it.remove();
			}
		}
	}

	/** Replace player's main inventory */
	public static void replaceInventory(PlayerEntity player) {
		player.inventoryMenu = new ContainerSAPlayer(player.inventory, !player.level.isClientSide, player);
		player.containerMenu = player.inventoryMenu;
	}
	
	/**Add tooltip when crafting shulker boxes*/
	@SubscribeEvent
	public static void onShulkerCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.getCrafting() != null && event.getCrafting().getItem() instanceof BlockItem &&
				((BlockItem)event.getCrafting().getItem()).getBlock() instanceof ShulkerBoxBlock) 
			Utils.updateTooltip(event.getCrafting(), true);
	}
	
}