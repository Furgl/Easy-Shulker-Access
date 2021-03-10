package furgl;

import furgl.packets.CPacketOpenShulkerBox;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy {

	public void setup(FMLCommonSetupEvent event) {
		this.registerPackets();
		this.registerListeners();
	}

	/**Register event listeners*/
	private void registerListeners() {
		//MinecraftForge.EVENT_BUS.register(new Config());
	}

	/**Register packets*/
	private void registerPackets() { 
		int id = 0;
		ShulkerAccess.NETWORK.registerMessage(id++, CPacketOpenShulkerBox.class, CPacketOpenShulkerBox::encode, CPacketOpenShulkerBox::decode, CPacketOpenShulkerBox.Handler::handle);
	}

}