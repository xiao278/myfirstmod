package kx.myfirstmod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class SummonLightning {
    public static Runnable getRunnable(World world, BlockPos blockPos, int checkColumn) {
        return () -> {
            summon(world, blockPos, checkColumn);
        };
    }
    public static void summon(World world, BlockPos blockPos, int checkColumn) {
        BlockPos topBlock = null;
        if (checkColumn <= 0) {
            topBlock = blockPos;
        }
        else {
            topBlock = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(blockPos.getX(), 0, blockPos.getZ())).down();
        }
        if (Math.abs(blockPos.getY() - topBlock.getY()) > checkColumn) {
            return;
        }
        Vec3d hitPos = topBlock.toCenterPos().add(0,0.5,0);
        LightningEntity lightningBolt = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightningBolt.setPosition(hitPos);
        world.spawnEntity(lightningBolt);
    }
}
