package kx.myfirstmod;

import kx.myfirstmod.entities.*;
import kx.myfirstmod.items.*;
import kx.myfirstmod.particles.HelicalParticleFactory;
import kx.myfirstmod.particles.ModParticles;
import kx.myfirstmod.rendering.*;
import kx.myfirstmod.utils.BlockGlowRenderer;
import kx.myfirstmod.utils.EffectGemColorTint;
import kx.myfirstmod.utils.ParticleSpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
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

        ModelPredicateProviderRegistry.register(ModItems.BEAM_WEAPON, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            if (entity.getActiveItem() != stack) return 0;

            return BeamWeapon.getPullProgress(entity, stack);
        });
        ModelPredicateProviderRegistry.register(ModItems.BEAM_WEAPON, new Identifier("charged"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return BeamWeapon.getIsCharged(stack) ? 1 : 0;
        });

        // Option A: Use your own sprite set
        ParticleFactoryRegistry.getInstance().register(ModParticles.HELICAL_PARTICLE, HelicalParticleFactory::new);
        EntityRendererRegistry.register(ModEntityTypes.ARROW_RAIN_ENTITY, ArrowRainEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.EFFECT_GEM_PROJECTILE_ENTITY, EffectGemProjectileEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.BEAM_WEAPON_ENTITY, BeamWeaponProjectileRenderer::new);
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((a,b,c,d) -> {
//            if (b instanceof ) {
//
//            }
//            c.register(new BeamWeaponFeatureRenderer<PlayerEntity, EntityModel<PlayerEntity>>((FeatureRenderer<Entity, PlayerEntityModel<PlayerEntity>>) b));
//        });

        BlockGlowRenderer.register();
        ParticleSpawnPacket.registerClientListener();
        EffectGemColorTint.register();
        BeamWeaponFeatureRenderer.register();
    }


    private void onResourcesReady() {
        System.out.println("ResourceManager is now available");
        EntityRendererRegistry.register(ModEntityTypes.GUARDIAN_LASER_ENTITY, GuardianLaserEntityRenderer::new);
    }
}
