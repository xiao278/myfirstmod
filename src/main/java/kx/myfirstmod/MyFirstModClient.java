package kx.myfirstmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

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
        ModelPredicateProviderRegistry.register(ModItems.GUARDIAN_LASER, new Identifier("glow"), (stack, world, entity, seed) -> {
            GuardianLaserEntity tracker = (GuardianLaserEntity) ((GuardianLaser) stack.getItem()).getHook();
            if (tracker == null) return 0;
            if (!tracker.hasBeamTarget() || tracker.isRemoved()) return 0;
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).getInventory().getStack(((PlayerEntity) entity).getInventory().selectedSlot) != stack) return 0;
            return (tracker.getBeamTicks() / tracker.getWarmupTime());
        });
    }

    private void onResourcesReady() {
        System.out.println("ResourceManager is now available");
        EntityRendererRegistry.register(ModEntityTypes.GUARDIAN_LASER_ENTITY, GuardianLaserEntityRenderer::new);
    }
}
