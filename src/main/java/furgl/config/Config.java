package furgl.config;

import java.io.File;

import furgl.ShulkerAccess;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;

	public static void preInit(final File file) {
		config = new Configuration(file);
		config.load();
		syncConfig();
		config.save();
	}

	public static void syncConfig() {
		// room for future config options
	}

	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(ShulkerAccess.MODID)) {
			syncConfig();
			config.save();
		}
	}

	public static void reload() {
		Config.config.load();
		Config.syncConfig();
		Config.config.save();
	}

}