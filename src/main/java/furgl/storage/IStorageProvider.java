package furgl.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStorageProvider extends INBTSerializable<NBTTagCompound> {

	/**Get storage*/
	public InfiniteStackHandler getStorage();
	
}