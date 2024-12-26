package kx.myfirstmod.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BlockDetector {
    public static BlockPos getBlockLookingAt(World world, PlayerEntity player, double maxDistance) {
        Vec3d start = player.getCameraPosVec(1.0F); // Get player's eye position
        Vec3d direction = player.getRotationVec(1.0F); // Get player's look direction
        Vec3d end = start.add(direction.multiply(maxDistance)); // Calculate the ray end point

        // Perform raycast
        BlockHitResult hitResult = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        // Check if the ray hit a block
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getBlockPos();
        }

        // Return null if no block was hit
        return null;
    }
}