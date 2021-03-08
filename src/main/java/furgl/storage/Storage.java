package furgl.storage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;

public class Storage implements IStorageProvider {

	private InfiniteStackHandler itemHandler = new InfiniteStackHandler();
	/**Should picked up items go into cloud storage*/
	public boolean pickUpItems;
	/**Should opening inventory show cloud storage (or normal inv)*/
	public boolean showCloudStorage;

	public Storage() {}

	@Override
	public InfiniteStackHandler getStorage() {
		return itemHandler;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// inv (items)
		NBTBase invNbt = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage()
				.writeNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.itemHandler, null);
		nbt.setTag("inv", invNbt);
		// pickUpItems
		nbt.setBoolean("pickUpItems", this.pickUpItems);
		// showCloudStorage
		nbt.setBoolean("showCloudStorage", this.showCloudStorage);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// inv (items)
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.getStorage()
		.readNBT(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler, null, nbt.getTag("inv"));
		// pickUpItems
		this.pickUpItems = nbt.getBoolean("pickUpItems");
		// showCloudStorage
		this.showCloudStorage = nbt.getBoolean("showCloudStorage");
	}

}