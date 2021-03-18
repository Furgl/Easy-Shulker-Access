package furgl.utils;

import javax.annotation.Nullable;

import cpw.mods.ironchest.common.blocks.shulker.IronShulkerBoxType;
import furgl.ShulkerAccess;
import furgl.packets.SPacketOpenIronShulkerBox;
import furgl.shulkerBox.ShulkerBoxListener;
import furgl.shulkerBox.TileEntitySAShulkerBox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Utils {
	
	// Iron Chest classes
	private static final String IRON_SHULKER_ITEM = "cpw.mods.ironchest.common.items.shulker.ItemIronShulkerBox";
	private static final String IRON_SHULKER_GUI = "cpw.mods.ironchest.client.gui.shulker.GUIShulkerChest";

	/**Is this item a shulker box (vanilla or modded)*/
	public static boolean isShulkerBox(ItemStack stack) {
		return stack != null && !stack.isEmpty() && (stack.getItem() instanceof ItemShulkerBox || stack.getItem().getClass().getName().equals(IRON_SHULKER_ITEM));
	}

	/**Is this container a shulker box (vanilla or modded)*/
	@SideOnly(Side.CLIENT)
	public static boolean isShulkerContainer(GuiContainer container) {
		return container != null && (container instanceof GuiShulkerBox || container.getClass().getName().equals(IRON_SHULKER_GUI));
	}
	
	/**Get the color of this shulker box*/
	@Nullable
	public static EnumDyeColor getColor(ItemStack stack) {
		// TODO
		return null;
	}

	/**Is this right-clicking on a shulker box*/
	public static boolean tryOpeningShulkerBox(int slotId, int dragType, ClickType clickTypeIn, EntityPlayerMP player, ItemStack stack) {
		if (isShulkerBox(stack)) {
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
	public static void openShulkerBox(EntityPlayerMP player, ItemStack stack) {
		// iron chests
		if (stack.getItem().getClass().getName().equals(IRON_SHULKER_ITEM)) {
			IronShulkerBoxType type = IronShulkerBoxType.VALUES[stack.getMetadata()];
			TileEntityLockableLoot te;
			try {
				// create like this so as to not load in the class unless necessary (in case Iron Chest is not installed)
				te = (TileEntityLockableLoot) Class.forName("furgl.shulkerBox.TileEntitySAIronShulkerBox").getConstructor(World.class, ItemStack.class, IronShulkerBoxType.class).newInstance(player.world, stack, type);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			player.closeContainer();
			player.getNextWindowId();
			ShulkerAccess.network.sendTo(new SPacketOpenIronShulkerBox(player.currentWindowId, stack), player);
			player.openContainer = te.createContainer(player.inventory, player);
			player.openContainer.windowId = player.currentWindowId;
			player.openContainer.addListener(player);
			player.openContainer.addListener(new ShulkerBoxListener(player, stack));
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
		}
		// vanilla
		else {
			TileEntitySAShulkerBox te = new TileEntitySAShulkerBox(player.world, stack);
			player.closeContainer(); // instead of closeScreen to prevent mouse moving
			player.getNextWindowId();
			player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, te.getGuiID(), te.getDisplayName(), te.getSizeInventory()));
			player.openContainer = te.createContainer(player.inventory, player);
			player.openContainer.windowId = player.currentWindowId;
			player.openContainer.addListener(player);
			player.openContainer.addListener(new ShulkerBoxListener(player, stack));
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
		}
		player.connection.sendPacket(new SPacketCustomSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN.getRegistryName().toString(), SoundCategory.BLOCKS, player.posX, player.posY, player.posZ, 0.5f, 1.0f));
	}

	public static final String OPEN_TEXT = TextFormatting.GOLD+""+TextFormatting.ITALIC+"Right-click to open";
	public static final String CLOSE_TEXT = TextFormatting.RED+""+TextFormatting.ITALIC+"Right-click to close";

	/**Add "Right-click to open/close" to tooltip*/
	public static void updateTooltip(ItemStack stack, boolean open) {
		if (isShulkerBox(stack)) {
			String newText = open ? OPEN_TEXT : CLOSE_TEXT;
			String oldText = open ? CLOSE_TEXT : OPEN_TEXT;
			// look for "Right-click to close/open" and replace
			NBTTagList list = stack.getOrCreateSubCompound("display").getTagList("Lore", NBT.TAG_STRING);
			for (int i=0; i<list.tagCount(); ++i)
				if (list.getStringTagAt(i).contains(oldText)) {
					list.set(i, new NBTTagString(newText));
					return;
				}
				else if (list.getStringTagAt(i).contains(newText))
					return;
			// if not found, add it
			Utils.appendItemStackLore(stack, false, newText);
		}
	}

	/**Append lore onto an itemstack with existing lore*/
	public static ItemStack appendItemStackLore(ItemStack stack, boolean hideFlags, String... lore) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound nbt2 = stack.getTagCompound();
		NBTTagCompound nbt1 = nbt2.hasKey("display") ? nbt2.getCompoundTag("display") : new NBTTagCompound();
		NBTTagList list = nbt1.getTagList("Lore", 8);	
		for (String string : lore)
			list.appendTag(new NBTTagString(TextFormatting.RESET+string));
		nbt1.setTag("Lore", list);
		nbt2.setTag("display", nbt1);
		if (hideFlags && !nbt2.hasKey("HideFlags"))
			nbt2.setInteger("HideFlags", 32);
		//else if (!hideFlags)
		//nbt2.removeTag("HideFlags"); removed v1.74.5 so claim blocks shovel in shop doesn't show tooltip
		stack.setTagCompound(nbt2);
		return stack;
	}

	/**Set an itemstack's name and lore*/
	public static ItemStack setItemStackLore(ItemStack stack, boolean hideFlags, @Nullable String name, String... lore) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound nbt1 = new NBTTagCompound();
		NBTTagCompound nbt2 = stack.getTagCompound();
		NBTTagList list = new NBTTagList();	
		for (String string : lore)
			list.appendTag(new NBTTagString(TextFormatting.RESET+string));
		nbt1.setTag("Lore", list);
		if (name != null)
			nbt1.setTag("Name", new NBTTagString(TextFormatting.RESET+""+name));
		nbt2.setTag("display", nbt1);
		if (hideFlags)
			nbt2.setInteger("HideFlags", 127); // does not hide "dyed" until like 1.16.2...
		else
			nbt2.removeTag("HideFlags");
		stack.setTagCompound(nbt2);
		return stack;
	}

}