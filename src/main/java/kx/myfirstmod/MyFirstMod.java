//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kx.myfirstmod;

import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.utils.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFirstMod implements ModInitializer {
	public static final String MOD_ID = "myfirstmod";
	public static final Logger LOGGER = LoggerFactory.getLogger("myfirstmod");

	public MyFirstMod() {
	}

	@Override
	public void onInitialize() {
		ModEntityTypes.initialize();
		ModItems.initialize();
		TaskScheduler.initialize();
		ModSounds.initialize();
	}
}
