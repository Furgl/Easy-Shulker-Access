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
				player.closeContainer();
			}
			// selected shulker box - open container
			else {
				ItemStack stack = player.inventory.getStackInSlot(packet.slot);
				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem &&
						((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
					TileEntityShulkerAccessBox te = new TileEntityShulkerAccessBox(stack);
					te.setWorldAndPos(player.world, BlockPos.ZERO);
					switchToContainerSilently(player, te);
					player.openContainer.addListener(new ShulkerBoxListener(player, stack));
				}
			}
			ctx.get().setPacketHandled(true);
		}
		
		private static void switchToContainerSilently(ServerPlayerEntity player, INamedContainerProvider te) {
			//player.openContainer(p_213829_1_)
			// open gui manually (instead of displayGUIChest()) so it doesn't reset mouse position
			player.closeContainer();
			player.getNextWindowId();
			player.openContainer = te.createMenu(player.currentWindowId, player.inventory, player);
			player.connection.sendPacket(new SOpenWindowPacket(player.openContainer.windowId, player.openContainer.getType(), te.getDisplayName()));
			//player.connection.sendPacket(new SOpenWindowPacket(player.currentWindowId, ((INamedContainerProvider)te).getGuiID(), te.getDisplayName(), te.getSizeInventory()));
			//player.openContainer.windowId = player.currentWindowId;
			player.openContainer.addListener(player);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
		}
	}

}
