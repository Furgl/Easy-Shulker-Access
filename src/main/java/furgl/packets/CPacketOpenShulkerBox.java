package furgl.packets;

import java.util.function.Supplier;

import furgl.shulkerBox.ShulkerBoxListener;
import furgl.shulkerBox.TileEntityShulkerAccessBox;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CPacketOpenShulkerBox {

	public int slot;

	public CPacketOpenShulkerBox() {}

	public CPacketOpenShulkerBox(int slot) {
		this.slot = slot;
	}
	
	public static void encode(CPacketOpenShulkerBox packet, PacketBuffer buf) {
		buf.writeInt(packet.slot);
	}

	public static CPacketOpenShulkerBox decode(PacketBuffer buf) {
		CPacketOpenShulkerBox packet = new CPacketOpenShulkerBox();
		packet.slot = buf.readInt();
		return packet;
	}

	public static class Handler {

		public static void handle(CPacketOpenShulkerBox packet, Supplier<NetworkEvent.Context> ctx) {
			ServerPlayerEntity player = ctx.get().getSender();
			// deselected shulker box - close container
			if (packet.slot == -1) {
				player.doCloseContainer();
			}
			// selected shulker box - open container
			else {
				ItemStack stack = player.inventory.getItem(packet.slot);
				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem &&
						((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
					TileEntityShulkerAccessBox te = new TileEntityShulkerAccessBox(stack);
					te.setLevelAndPosition(player.level, BlockPos.ZERO);
					switchToContainerSilently(player, te);
					player.containerMenu.addSlotListener(new ShulkerBoxListener(player, stack));
				}
			}
			ctx.get().setPacketHandled(true);
		}
		
		private static void switchToContainerSilently(ServerPlayerEntity player, INamedContainerProvider te) {
			//player.openMenu(p_71112_1_, p_71112_2_, p_71112_3_);
			// open gui manually (instead of displayGUIChest()) so it doesn't reset mouse position
			player.doCloseContainer();
			player.nextContainerCounter();
			player.containerMenu = te.createMenu(player.containerCounter, player.inventory, player);
			player.connection.send(new SOpenWindowPacket(player.containerMenu.containerId, player.containerMenu.getType(), te.getDisplayName()));
			player.containerMenu.addSlotListener(player);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.containerMenu));
		}
	}

}
