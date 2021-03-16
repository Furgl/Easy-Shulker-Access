package furgl;

import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import furgl.packets.CPacketOpenShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(value = ShulkerAccess.MODID)
public class ShulkerAccess {
	
	// NOTE: doesn't work in server-side-only creative bc creative uses a clientside container

	public static final String MODID = "shulker_access";
	public static final String MODNAME = "Easy Shulker Access";
	public static final String VERSION = "1.1";

	public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);
	public static ModContainer modContainer;
	private static final String PROTOCOL_VERSION = NetworkRegistry.ABSENT;
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ShulkerAccess.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public ShulkerAccess() {
		modContainer = ModLoadingContext.get().getActiveContainer();
		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		registerPackets();
	}
	
	/**Register packets*/
	private void registerPackets() { 
		int id = 0;
		ShulkerAccess.NETWORK.registerMessage(id++, CPacketOpenShulkerBox.class, CPacketOpenShulkerBox::encode, CPacketOpenShulkerBox::decode, CPacketOpenShulkerBox.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

}