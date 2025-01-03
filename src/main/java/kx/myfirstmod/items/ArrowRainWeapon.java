package kx.myfirstmod.items;

import kx.myfirstmod.entities.ArrowRainEntity;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.utils.BlockDetector;
import kx.myfirstmod.utils.EntityDetector;
import kx.myfirstmod.utils.TaskScheduler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class ArrowRainWeapon extends Item {
    public ArrowRainWeapon(Settings settings) {
        super(settings);
    }
    public static final double range = 64;
    public static final int projectile_count = 15;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, range, 25);
        Random random = world.getRandom();
        if (target != null) {
            for (int i = 0; i < projectile_count; i++) {
                TaskScheduler.schedule(() -> {
                    double x_variance = (random.nextDouble() - 0.5) * 24;
                    double z_variance = (random.nextDouble() - 0.5) * 24;
                    world.spawnEntity(new ArrowRainEntity(
                            ModEntityTypes.ARROW_RAIN_ENTITY,
                            world,
                            user,
                            new Vec3d(target.getX() + x_variance, 255, target.getZ() + z_variance),
                            target
                    ));
                }, i + 1);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        BlockPos bPos = BlockDetector.getBlockLookingAt(world, user, range);
        if (bPos == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        for (int i = 0; i < projectile_count; i++) {
            TaskScheduler.schedule(() -> {
                double x_variance = (random.nextDouble() - 0.5) * 24;
                double z_variance = (random.nextDouble() - 0.5) * 24;
                world.spawnEntity(new ArrowRainEntity(
                        ModEntityTypes.ARROW_RAIN_ENTITY,
                        world,
                        user,
                        new Vec3d(bPos.getX() + x_variance, 255, bPos.getZ() + z_variance),
                        bPos
                ));
            }, i + 1);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private void spawnArrows(World world, BlockPos coordinates, PlayerEntity user) {
        world.spawnEntity(new ArrowEntity(world, coordinates.getX(), coordinates.getY(), coordinates.getZ()));
    }
}
