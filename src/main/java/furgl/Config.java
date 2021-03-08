package furgl;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;
	
	public static boolean needEmptyHand;
	public static int maxDistance;

	public static void preInit(final File file) {
		config = new Configuration(file);
		config.load();
		syncConfig();
		config.save();
	}

	public static void syncConfig() {
		Property prop;

		prop = config.get(Configuration.CATEGORY_GENERAL, "Need Empty Hand", false, "Only instant climb if your hand is empty");
		needEmptyHand = prop.getBoolean();
		
		prop = config.get(Configuration.CATEGORY_GENERAL, "Max Distance", -1, "Max distance that players can instant climb. -1 for no limit", -1, Integer.MAX_VALUE);
		maxDistance = prop.getInt();
	}

	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(CloudStorage.MODID)) {
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