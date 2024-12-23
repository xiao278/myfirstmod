package kx.myfirstmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class GuardianLaser extends Item {
    private static final int range = 64;
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

        LivingEntity target = EntityDetector.findClosestCrosshairEntity(world, user, this.range, 30);

        if (target != null && (hook == null || hook.isRemoved())) {
            ItemStack stack = user.getStackInHand(hand);
            if (!world.isClient()) {
                GuardianLaserEntity GLEntity = new GuardianLaserEntity(ModEntityTypes.GUARDIAN_LASER_ENTITY, world, target, user);
                world.spawnEntity(GLEntity);
                this.hook = GLEntity;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(stack);
        }
        else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient()) {
            if (hook != null && (!hook.hasBeamTarget() || !hook.isAlive() || hook.isRemoved())) {
                hook.stopUsing();
                hook = null;
            }
        }
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient()) {
            if (hook != null) {
                hook.stopUsing();
                hook = null;
            }
        }
    }

    public Entity getHook() {
        return this.hook;
    }
}
