package furgl;

import java.io.File;

import org.apache.logging.log4j.Logger;

import furgl.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = ShulkerAccess.MODID,name=ShulkerAccess.MODNAME,version=ShulkerAccess.VERSION)
public class ShulkerAccess {

	public static final String MODID = "shulker_access";
	public static final String MODNAME = "Easy Shulker Access";
	public static final String VERSION = "1.0";

	public static Logger logger;
	public static File configFile;
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	@SidedProxy(clientSide = "furgl.client.ClientProxy", serverSide = "furgl.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		configFile = event.getSuggestedConfigurationFile();
		Config.preInit(configFile);
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}