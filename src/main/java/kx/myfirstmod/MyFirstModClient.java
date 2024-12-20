package kx.myfirstmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.item.Items;

public class MyFirstModClient implements ClientModInitializer {
    public void onInitializeClient() {
        // Register your custom item renderer
//        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.LIGHTNING_STICK, new LightningStickItemRenderer());
    }
}
