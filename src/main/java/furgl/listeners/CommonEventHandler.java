package furgl.listeners;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.Lists;

import furgl.containers.ContainerSAPlayer;
import furgl.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class CommonEventHandler {

	/**Newly created players that need to have their inventories replaced*/
	private static ArrayList<EntityPlayer> newPlayers = Lists.newArrayList();

	@SubscribeEvent
	public static void onPlayerCreated(EntityEvent.EntityConstructing event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote) 
			newPlayers.add((EntityPlayer)event.getEntity());
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event) {
		if (!newPlayers.isEmpty()) {
			Iterator<EntityPlayer> it = newPlayers.iterator();
			while (it.hasNext()) {
				EntityPlayer player = it.next();
				if (player.ticksExisted > 5)
					replaceInventory(player);
				it.remove();
			}
		}
	}

	/** Replace player's main inventory */
	public static void replaceInventory(EntityPlayer player) {
		player.inventoryContainer = new ContainerSAPlayer(player.inventory, !player.world.isRemote, player);
		player.openContainer = player.inventoryContainer;
	}

	/** Add tooltip when crafting shulker boxes */
	@SubscribeEvent
	public static void onShulkerCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting != null && event.crafting.getItem() instanceof ItemShulkerBox)
			Utils.updateTooltip(event.crafting, true);
	}

}