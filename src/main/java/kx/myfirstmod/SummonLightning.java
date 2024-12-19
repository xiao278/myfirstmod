package kx.myfirstmod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonLightning {
    public static Runnable getRunnable(World world, BlockPos blockPos, int checkColumn) {
        return () -> {
            summon(world, blockPos, checkColumn);
        };
    }
    public static void summon(World world, BlockPos blockPos, int checkColumn) {
        Vec3d hitPos = blockPos.toCenterPos().add(0,0.5,0);
        LightningEntity lightningBolt = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightningBolt.setPosition(hitPos);
        world.spawnEntity(lightningBolt);
    }
}
