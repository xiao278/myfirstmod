package kx.myfirstmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class GuardianLaser extends Item {
    private static final int range = 60;
    private GuardianLaserEntity hook;
    public GuardianLaser(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        Vec3d uPos = user.getPos();
        Box searchBox = new Box(
                uPos.x - range, uPos.y - range, uPos.z - range,
                uPos.x + range, uPos.y + range, uPos.z + range
        );
        List<Entity> potentialTargets = world.getOtherEntities(user, searchBox, entity -> (entity instanceof PlayerEntity || entity instanceof MobEntity));

        LivingEntity target = null;
        double minCriteria = Float.POSITIVE_INFINITY;
        for (Entity e: potentialTargets) {
            if (e.isAlive() && e.isLiving()) {
                double criteria = e.distanceTo(user);
                if (criteria < minCriteria && e instanceof LivingEntity) {
                    target = (LivingEntity) e;
                    minCriteria = criteria;
                }
            }
        }

        if (target != null) {
            if (!world.isClient()) {
                world.spawnEntity(new GuardianLaserEntity(ModEntityTypes.GUARDIAN_LASER_ENTITY, world, target, user));
            }
            ItemStack stack = user.getStackInHand(hand);
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

    }
}
