package kx.myfirstmod;

import kx.myfirstmod.entities.*;
import kx.myfirstmod.items.ArrowRainWeapon;
import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.GuardianLaser;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.utils.BlockGlowRenderer;
import kx.myfirstmod.utils.EffectGemColorTint;
import kx.myfirstmod.utils.ParticleSpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
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
            GuardianLaserEntity tracker = ((GuardianLaser) stack.getItem()).getHook(stack, world);
            if (tracker == null || tracker.getOwner() == null) return 0;
            if (!tracker.hasBeamTarget() || tracker.isRemoved()) return 0;
            if (!(entity instanceof  PlayerEntity)) return 0;
            return (tracker.getBeamTicks() / tracker.getWarmupTime());
        });

        ModelPredicateProviderRegistry.register(ModItems.ARROW_RAIN, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            if (entity.getActiveItem() != stack) return 0;

            int maxPullTicks = stack.getMaxUseTime(); // Typically 20
            int ticksPulled = maxPullTicks - entity.getItemUseTimeLeft();

            return ArrowRainWeapon.getPullProgress(ticksPulled);
        });

        ModelPredicateProviderRegistry.register(ModItems.EFFECT_GEM, new Identifier("unstable"), (stack, world, entity, seed) -> {
            return EffectGem.getIsUnstable(stack) ? 1 : 0;
        });
        ModelPredicateProviderRegistry.register(ModItems.EFFECT_GEM, new Identifier("thrown"), (stack, world, entity, seed) -> {
            return EffectGem.getIsProjectile(stack) ? 1 : 0;
        });

        EntityRendererRegistry.register(ModEntityTypes.ARROW_RAIN_ENTITY, ArrowRainEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.EFFECT_GEM_PROJECTILE_ENTITY, EffectGemProjectileEntityRenderer::new);
        BlockGlowRenderer.register();
        ParticleSpawnPacket.registerClientListener();
        EffectGemColorTint.register();
    }

    private void onResourcesReady() {
        System.out.println("ResourceManager is now available");
        EntityRendererRegistry.register(ModEntityTypes.GUARDIAN_LASER_ENTITY, GuardianLaserEntityRenderer::new);
    }
}
