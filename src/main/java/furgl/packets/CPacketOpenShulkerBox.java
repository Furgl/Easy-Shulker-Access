package furgl.packets;

import java.util.function.Supplier;

import furgl.utils.Utils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
			// deselected shulker box - close container (client opens inventory)
			if (packet.slot == -1) {
				player.connection.send(new SPlaySoundPacket(SoundEvents.SHULKER_BOX_CLOSE.getRegistryName(), SoundCategory.BLOCKS, player.position(), 0.5f, 1.0f));
				player.doCloseContainer();
			}
			// selected shulker box - open container
			else {
				ItemStack stack = player.inventory.getItem(packet.slot);
				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem &&
						((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock) 
					Utils.openShulkerBox(player, stack);
			}
			ctx.get().setPacketHandled(true);
		}

	}

}