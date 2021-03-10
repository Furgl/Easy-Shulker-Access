package furgl;

import furgl.config.Config;
import furgl.packets.CPacketOpenShulkerBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		this.registerPackets();
		this.registerListeners();
	}
	
	/**Register event listeners*/
	private void registerListeners() {
		MinecraftForge.EVENT_BUS.register(new Config());
	}
	
	/**Register packets*/
	private void registerPackets() { // Side is where the packets goes TO
		int id = 0;
		ShulkerAccess.network.registerMessage(CPacketOpenShulkerBox.Handler.class, CPacketOpenShulkerBox.class, id++, Side.SERVER);
	}

	public void init(FMLInitializationEvent event) {}

	public void postInit(FMLPostInitializationEvent event) {}
	
}