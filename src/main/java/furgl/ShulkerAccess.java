package furgl;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import furgl.client.ClientProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(value = ShulkerAccess.MODID)
public class ShulkerAccess {

	public static final String MODID = "shulker_access";
	public static final String MODNAME = "Easy Shulker Access";
	public static final String VERSION = "1.0";

	public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);
	public static ModContainer MOD_CONTAINER;
	public static File configFile;
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ShulkerAccess.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	@SuppressWarnings("deprecation")
	public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public ShulkerAccess() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
	}
	
	private void commonSetup(FMLCommonSetupEvent event) {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
		//MOD_CONTAINER.addConfig(new Config(Type.COMMON, ));
		//Config.preInit(configFile);
		proxy.setup(event);
	}

}