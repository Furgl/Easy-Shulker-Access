package furgl.packets;

import furgl.shulkerBox.ShulkerBoxListener;
import furgl.shulkerBox.TileEntityShulkerAccessBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketOpenShulkerBox implements IMessage {

	public int slot;

	public CPacketOpenShulkerBox() {}

	public CPacketOpenShulkerBox(int slot) {
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.slot);
	}

	public static class Handler implements IMessageHandler<CPacketOpenShulkerBox, IMessage> {
		@Override
		public IMessage onMessage(final CPacketOpenShulkerBox packet, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {

				@Override
				public void run() {
					// deselected shulker box - close container
					if (packet.slot == -1) {
						ctx.getServerHandler().player.closeContainer();
					}
					// selected shulker box - open container
					else {
						ItemStack stack = ctx.getServerHandler().player.inventory.getStackInSlot(packet.slot);
						if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemShulkerBox) {
							TileEntityShulkerAccessBox te = new TileEntityShulkerAccessBox(stack);
							te.setWorld(ctx.getServerHandler().player.world);
							switchToContainerSilently(ctx.getServerHandler().player, te);
							ctx.getServerHandler().player.openContainer.addListener(new ShulkerBoxListener(ctx.getServerHandler().player, stack));
						}
					}
				}
			});
			return null;
		}

		private void switchToContainerSilently(EntityPlayerMP player, IInventory inv) {
			//player.displayGUIChest(te);
			// open gui manually (instead of displayGUIChest()) so it doesn't reset mouse position
			player.closeContainer();
			player.getNextWindowId();
			player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, ((IInteractionObject)inv).getGuiID(), inv.getDisplayName(), inv.getSizeInventory()));
			player.openContainer = ((IInteractionObject)inv).createContainer(player.inventory, player);
			player.openContainer.windowId = player.currentWindowId;
			player.openContainer.addListener(player);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
		}
	}
}
