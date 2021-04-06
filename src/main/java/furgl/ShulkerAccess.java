package furgl;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import furgl.packets.CPacketOpenShulkerBox;
import furgl.packets.SPacketOpenIronShulkerBox;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ShulkerAccess.MODID,name=ShulkerAccess.MODNAME,version=ShulkerAccess.VERSION,acceptableRemoteVersions="*",dependencies="after:ironchest")
public class ShulkerAccess {

	// v1.2 added Iron Chest compatibility
	// prob discontinuing this tho - https://www.curseforge.com/minecraft/mc-mods/quick-shulker is the same thing
	
	// NOTE: doesn't work in server-side-only creative bc creative uses a clientside container
	// BUG: player's inventory renders offset for Diamond Shulker Boxes for some reason

	public static final String MODID = "shulker_access";
	public static final String MODNAME = "Easy Shulker Access";
	public static final String VERSION = "1.1.1";

	public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public ShulkerAccess() {
		registerPackets();
	}

	/**Register packets*/
	private void registerPackets() { 
		int id = 0;
		// client right-clicks shulker box -> tell server to open/close container
		ShulkerAccess.network.registerMessage(CPacketOpenShulkerBox.Handler.class, CPacketOpenShulkerBox.class, id++, Side.SERVER);
		// server opens iron shulker box -> tell client to open gui
		if (Loader.isModLoaded("ironchest"))
			ShulkerAccess.network.registerMessage(SPacketOpenIronShulkerBox.Handler.class, SPacketOpenIronShulkerBox.class, id++, Side.CLIENT);
	}

}