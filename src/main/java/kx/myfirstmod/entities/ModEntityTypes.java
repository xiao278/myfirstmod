package kx.myfirstmod.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {
    public static void initialize() {}

    public static final EntityType<ArrowRainEntity> ARROW_RAIN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("myfirstmod", "arrow_rain_entity"),
            FabricEntityTypeBuilder.<ArrowRainEntity>create(SpawnGroup.MISC, ArrowRainEntity::new)
                    .dimensions(EntityDimensions.fixed(1F, 1F)) // Arrow size
                    .trackRangeChunks(10) // Range in chunks where the entity is tracked
                    .trackedUpdateRate(1).build()
    );

    public static final EntityType<GuardianLaserEntity> GUARDIAN_LASER_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("myfirstmod","guardian_laser_entity"),
            FabricEntityTypeBuilder.<GuardianLaserEntity>create(SpawnGroup.MISC, GuardianLaserEntity::new)
                    .dimensions(EntityDimensions.fixed(0, 0))
                    .trackRangeChunks(8)
                    .trackedUpdateRate(1).build()
    );

    public static final EntityType<EffectGemProjectileEntity> EFFECT_GEM_PROJECTILE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("myfirstmod", "effect_gem_projectile_entity"),
            FabricEntityTypeBuilder.<EffectGemProjectileEntity>create(SpawnGroup.MISC, EffectGemProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2F,0.2F))
                    .trackRangeChunks(8)
                    .trackedUpdateRate(1)
                    .build()
    );

    public static final EntityType<BeamWeaponEntity> BEAM_WEAPON_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("myfirstmod", "beam_weapon_projectile_entity"),
            FabricEntityTypeBuilder.<BeamWeaponEntity>create(SpawnGroup.MISC, BeamWeaponEntity::new)
                    .dimensions(EntityDimensions.fixed(0.0F,0.0F))
                    .trackRangeChunks(8)
                    .trackedUpdateRate(1)
                    .build()
    );
}
