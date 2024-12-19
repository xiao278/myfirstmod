//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kx.myfirstmod;

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
		ModItems.initialize();
		TaskScheduler.initialize();
	}
}
