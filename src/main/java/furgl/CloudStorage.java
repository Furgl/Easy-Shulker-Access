package furgl;


import java.io.File;

import org.apache.logging.log4j.Logger;

import furgl.storage.StorageManager;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = CloudStorage.MODID,name=CloudStorage.MODNAME,version=CloudStorage.VERSION)
public class CloudStorage {
	
	// Could easily change stack size of items, but need to change max stack size in all containers and not
	// sure how to do that

	public static final String MODID = "cloud_storage";
	public static final String MODNAME = "Cloud Storage";
	public static final String VERSION = "1.0";

	public static Logger logger;
	public static File configFile;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		configFile = event.getSuggestedConfigurationFile();
		StorageManager.init();
		Config.preInit(configFile);
		MinecraftForge.EVENT_BUS.register(new Config());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) { 
		// change stack size of items (doesn't work for other containers though)
		//Item.REGISTRY.forEach(item -> item.setMaxStackSize(Integer.MAX_VALUE));
	}

}