package furgl.packets;

import furgl.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
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
					EntityPlayerMP player = ctx.getServerHandler().player;
					// deselected shulker box - close container (client opens inventory)
					if (packet.slot == -1) {
						player.connection.sendPacket(new SPacketCustomSound(SoundEvents.BLOCK_SHULKER_BOX_CLOSE.getRegistryName().toString(), SoundCategory.BLOCKS, player.posX, player.posY, player.posZ, 0.5f, 1.0f));
						player.closeContainer();
					}
					// selected shulker box - open container
					else {
						ItemStack stack = player.inventory.getStackInSlot(packet.slot);
						if (Utils.isShulkerBox(stack)) 
							Utils.openShulkerBox(player, stack);
					}
				}
			});
			return null;
		}
	}
	
}