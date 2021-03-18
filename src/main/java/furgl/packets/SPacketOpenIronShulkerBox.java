package furgl.packets;

import cpw.mods.ironchest.client.gui.shulker.GUIShulkerChest;
import cpw.mods.ironchest.common.blocks.shulker.IronShulkerBoxType;
import furgl.shulkerBox.TileEntitySAIronShulkerBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketOpenIronShulkerBox implements IMessage {

	public int windowId;
	public ItemStack stack;

	public SPacketOpenIronShulkerBox() {}

	public SPacketOpenIronShulkerBox(int windowId, ItemStack stack) {
		this.windowId = windowId;
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.windowId);
		ByteBufUtils.writeItemStack(buf, this.stack);
	}

	public static class Handler implements IMessageHandler<SPacketOpenIronShulkerBox, IMessage> { 
		
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(final SPacketOpenIronShulkerBox packet, final MessageContext ctx) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			IronShulkerBoxType type = IronShulkerBoxType.VALUES[packet.stack.getMetadata()];
			TileEntitySAIronShulkerBox te = new TileEntitySAIronShulkerBox(player.world, packet.stack, type);
			GUIShulkerChest gui = GUIShulkerChest.GUI.buildGUI(type, player.inventory, te);
			gui.inventorySlots = te.createContainer(player.inventory, player);
			gui.inventorySlots.windowId = packet.windowId;
			FMLCommonHandler.instance().showGuiScreen(gui);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
			return null;
		}
		
	}

}