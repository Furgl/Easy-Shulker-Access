package furgl;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import furgl.packets.CPacketOpenShulkerBox;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ShulkerAccess.MODID,name=ShulkerAccess.MODNAME,version=ShulkerAccess.VERSION,acceptableRemoteVersions="*")
public class ShulkerAccess {

	// NOTE: doesn't work in server-side-only creative bc creative uses a clientside container

	public static final String MODID = "shulker_access";
	public static final String MODNAME = "Easy Shulker Access";
	public static final String VERSION = "1.1";

	public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public ShulkerAccess() {
		registerPackets();
	}

	/**Register packets*/
	private void registerPackets() { 
		int id = 0;
		ShulkerAccess.network.registerMessage(CPacketOpenShulkerBox.Handler.class, CPacketOpenShulkerBox.class, id++, Side.SERVER);
	}

}