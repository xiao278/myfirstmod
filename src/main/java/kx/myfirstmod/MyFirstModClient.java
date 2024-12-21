package kx.myfirstmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class MyFirstModClient implements ClientModInitializer {
    private boolean initialized = false;
    @Override
    public void onInitializeClient() {
        // Register a client tick callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialized && MinecraftClient.getInstance().getResourceManager() != null) {
                initialized = true;
                onResourcesReady();
            }
        });
    }

    private void onResourcesReady() {
        System.out.println("ResourceManager is now available");
        EntityRendererRegistry.register(ModEntityTypes.GUARDIAN_LASER_ENTITY, GuardianLaserEntityRenderer::new);
    }
}
