package furgl.utils;

import javax.annotation.Nullable;

import furgl.shulkerBox.ShulkerBoxListener;
import furgl.shulkerBox.TileEntityShulkerAccessBox;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants.NBT;

public class Utils {

	/**Append lore onto an itemstack with existing lore*/
	public static ItemStack appendItemStackLore(ItemStack stack, String... lore) {
		return appendItemStackLore(stack, true, lore);
	}

	/**Append lore onto an itemstack with existing lore*/
	public static ItemStack appendItemStackLore(ItemStack stack, boolean hideFlags, String... lore) {
		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());
		CompoundNBT nbt2 = stack.getTag();
		CompoundNBT nbt1 = nbt2.contains("display") ? nbt2.getCompound("display") : new CompoundNBT();
		ListNBT list = nbt1.getList("Lore", 8);	
		for (String string : lore)
			list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET+string))));
		nbt1.put("Lore", list);
		nbt2.put("display", nbt1);
		if (hideFlags && !nbt2.contains("HideFlags"))
			nbt2.putInt("HideFlags", 32);
		//else if (!hideFlags)
		//nbt2.removeTag("HideFlags"); removed v1.74.5 so claim blocks shovel in shop doesn't show tooltip
		stack.setTag(nbt2);
		return stack;
	}

	/**Set an itemstack's name and lore*/
	public static ItemStack setItemStackLore(ItemStack stack, @Nullable String name, String... lore) {
		return setItemStackLore(stack, true, name, lore);
	}

	/**Set an itemstack's name and lore*/
	public static ItemStack setItemStackLore(ItemStack stack, boolean hideFlags, @Nullable String name, String... lore) {
		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());
		CompoundNBT nbt1 = new CompoundNBT();
		CompoundNBT nbt2 = stack.getTag();
		ListNBT list = new ListNBT();	
		for (String string : lore)
			list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET+string))));
		nbt1.put("Lore", list);
		if (name != null)
			nbt1.put("Name", StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.RESET+""+name))));
		nbt2.put("display", nbt1);
		if (hideFlags)
			nbt2.putInt("HideFlags", 127); // does not hide "dyed" until like 1.16.2...
		else
			nbt2.remove("HideFlags");
		stack.setTag(nbt2);
		return stack;
	}

	/**Is this right-clicking on a shulker box*/
	public static boolean tryOpeningShulkerBox(int slotId, int dragType, ClickType clickTypeIn, ServerPlayerEntity player, ItemStack stack) {
		if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem &&
				((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			// update tooltip
			Utils.updateTooltip(stack, true);
			// is the player right-clicking on a shulker box
			if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {
				openShulkerBox(player, stack);
				return true;
			}
		}
		return false;
	}

	/**Open this shulker box gui*/
	public static void openShulkerBox(ServerPlayerEntity player, ItemStack stack) {
		TileEntityShulkerAccessBox te = new TileEntityShulkerAccessBox(stack);
		te.setWorldAndPos(player.world, BlockPos.ZERO);
		player.closeContainer();
		player.getNextWindowId();
		player.openContainer = te.createMenu(player.currentWindowId, player.inventory, player);
		player.connection.sendPacket(new SOpenWindowPacket(player.openContainer.windowId, player.openContainer.getType(), te.getDisplayName()));
		//player.connection.sendPacket(new SOpenWindowPacket(player.currentWindowId, ((INamedContainerProvider)te).getGuiID(), te.getDisplayName(), te.getSizeInventory()));
		//player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addListener(player);
		player.openContainer.addListener(new ShulkerBoxListener(player, stack));
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
		player.connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_SHULKER_BOX_OPEN.getRegistryName(), SoundCategory.BLOCKS, player.getPositionVec(), 0.5f, 1.0f));
	}

	public static final String OPEN_TEXT = TextFormatting.GOLD+""+TextFormatting.ITALIC+"Right-click to open";
	public static final String CLOSE_TEXT = TextFormatting.RED+""+TextFormatting.ITALIC+"Right-click to close";

	/**Add "Right-click to open/close" to tooltip*/
	public static void updateTooltip(ItemStack stack, boolean open) {
		if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem &&
				((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
			String newText = open ? OPEN_TEXT : CLOSE_TEXT;
			String oldText = open ? CLOSE_TEXT : OPEN_TEXT;
			// look for "Right-click to close/open" and replace
			ListNBT list = stack.getOrCreateChildTag("display").getList("Lore", NBT.TAG_STRING);
			for (int i=0; i<list.size(); ++i)
				if (list.getString(i).contains(oldText)) {
					list.set(i, StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(newText))));
					return;
				}
				else if (list.getString(i).contains(newText))
					return;
			// if not found, add it
			Utils.appendItemStackLore(stack, false, newText);
		}
	}

}