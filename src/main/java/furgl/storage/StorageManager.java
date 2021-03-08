package furgl.storage;

import java.util.concurrent.Callable;

import furgl.CloudStorage;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class StorageManager {
	
	@CapabilityInject(IStorageProvider.class)
	public static final Capability<IStorageProvider> CAPABILITY = null;

    public static void init() {
        CapabilityManager.INSTANCE.register(IStorageProvider.class, new Capability.IStorage<IStorageProvider>() {
            @Override
            public NBTTagCompound writeNBT(Capability<IStorageProvider> capability, IStorageProvider instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IStorageProvider> capability, IStorageProvider instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound)
                    instance.deserializeNBT(((NBTTagCompound) nbt));
            }
        }, new Callable<IStorageProvider>() {
            @Override
            public IStorageProvider call() throws Exception {
                return new Storage();
            }
        });
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        public static final ResourceLocation NAME = new ResourceLocation(CloudStorage.MODID, "storage");

        private static final Storage INSTANCE = new Storage();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == CAPABILITY) {
                return CAPABILITY.cast(INSTANCE);
            }

            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return INSTANCE.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            INSTANCE.deserializeNBT(nbt);
        }
    }

}