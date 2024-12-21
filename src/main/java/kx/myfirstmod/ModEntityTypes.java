package kx.myfirstmod;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {
    public static void initialize() {

    }
    public static final EntityType<GuardianLaserEntity> GUARDIAN_LASER_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("myfirstmod","guardian_laser_entity"),
            FabricEntityTypeBuilder.<GuardianLaserEntity>create(SpawnGroup.MISC, (eType, world) -> new GuardianLaserEntity(eType, world))
                    .dimensions(EntityDimensions.fixed(0, 0))
                    .trackRangeChunks(8)
                    .trackedUpdateRate(1).build()
    );
}
