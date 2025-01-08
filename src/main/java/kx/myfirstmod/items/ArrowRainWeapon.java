package kx.myfirstmod.items;

import kx.myfirstmod.entities.ArrowRainEntity;
import kx.myfirstmod.entities.ModEntityTypes;
import kx.myfirstmod.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;


public class ArrowRainWeapon extends BowItem {
    public ArrowRainWeapon(Settings settings) {
        super(settings);
    }
    public static final int MAX_PULL_TICKS = 20;
    public static final double range = 100;
    public static final double angle = 6;
    public static final int projectile_count = 16;
    private static final String BLOCK_POS_KEY = "StoredArrowRainAimBlockPos";

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (world.isClient && clientPlayer != null && clientPlayer.getUuid() == user.getUuid()) {
            LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, range, angle, true);
            int ticksPulled = stack.getMaxUseTime() - user.getItemUseTimeLeft();
            BlockGlowRenderer.setPullProgress(ArrowRainWeapon.getPullProgress(ticksPulled));
            if (target != null) {
                BlockGlowRenderer.setEntity(target);
                BlockGlowRenderer.setBlockPos(null);
            }
            else {
                BlockPos block = BlockDetector.getBlockLookingAt(world, (PlayerEntity) user, range);
                BlockGlowRenderer.setEntity(null);
                BlockGlowRenderer.setBlockPos(block);
            }
//            System.out.println(user.getDisplayName());
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int maxPullTicks = stack.getMaxUseTime();
        int ticksPulled = maxPullTicks - user.getItemUseTimeLeft();
        if (world.isClient) {
            BlockGlowRenderer.setBlockPos(null);
            BlockGlowRenderer.setEntity(null);
            BlockGlowRenderer.setPullProgress(0);
//            LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, range, angle, true);
//            BlockPos block = BlockDetector.getBlockLookingAt(world, (PlayerEntity) user, range);
            return;
        }
        else {
            if (ticksPulled >= MAX_PULL_TICKS) {
                LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, range, angle, true);
                BlockPos block = BlockDetector.getBlockLookingAt(world, (PlayerEntity) user, range);
                ServerWorld sw = (ServerWorld) world;
                if (target != null) {
                    spawnArrows(world, target, (PlayerEntity) user);
                    shootParticles(sw, user);
                    playShootSound(world, (PlayerEntity) user);
                    return;
                }

                if (block == null) {
                    return;
                }
                playShootSound(world, (PlayerEntity) user);
                shootParticles(sw, user);
                spawnArrows(world, block, (PlayerEntity) user);
            }
        }
    }

    private Vec3d getSpawnCoords(Random random, Vec3d pos, PlayerEntity user) {
        double x_variance = (random.nextDouble() - 0.5) * 24;
        double z_variance = (random.nextDouble() - 0.5) * 24;
        return new Vec3d(pos.getX() + x_variance, pos.y + 256, pos.getZ() + z_variance);
    }

    private void spawnArrows(World world, BlockPos bPos, PlayerEntity user) {
        Random random = world.getRandom();
        for (int i = 0; i < projectile_count; i++) {
            TaskScheduler.schedule(() -> {
                world.spawnEntity(new ArrowRainEntity(
                        ModEntityTypes.ARROW_RAIN_ENTITY,
                        world,
                        user,
                        getSpawnCoords(random, bPos.toCenterPos(), user),
                        bPos
                ));
            }, i + 1);
        }
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / MAX_PULL_TICKS;
//        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public void playShootSound(World world, PlayerEntity user) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) +  0.5F);
    }

    public void shootParticles(ServerWorld world, LivingEntity user) {
        Random rand = world.getRandom();
        Vec3d root = user.getEyePos().add(user.getHandPosOffset(this).multiply(0.5));
        for (int i = 0; i < 10; i++) {
            Vec3d vel = user.getRotationVector().add(new Vec3d(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).multiply(0.25)).multiply(0.5);
            world.addParticle(ParticleTypes.POOF,
                    root.x, root.y, root.z,
                    vel.x, vel.y, vel.z
            );
            ParticleSpawnPacket.send(world, ParticleTypes.POOF, root, vel);
        }
    }

    private void spawnArrows(World world, LivingEntity target, PlayerEntity user) {
        Random random = world.getRandom();
        for (int i = 0; i < projectile_count; i++) {
            TaskScheduler.schedule(() -> {
                world.spawnEntity(new ArrowRainEntity(
                        ModEntityTypes.ARROW_RAIN_ENTITY,
                        world,
                        user,
                        getSpawnCoords(random, target.getPos(), user),
                        target
                ));
            }, i + 1);
        }
    }
}
