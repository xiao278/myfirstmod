package kx.myfirstmod.items;

import kx.myfirstmod.entities.CustomShulkerBulletEntity;
import kx.myfirstmod.utils.EntityDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShulkerStaff extends Item {

    public ShulkerStaff(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        // serverside stuff
        Entity target = EntityDetector.findClosestCrosshairEntity(world, user, 32, 50);
        if (target == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        Direction.Axis initialDir = user.getPitch() <= -45 ? Direction.UP.getAxis() : user.getHorizontalFacing().getAxis();
        for (int i = 0; i < 8; i++) {
            world.spawnEntity(new CustomShulkerBulletEntity(world, user, target, initialDir, 0.6));
        }
        user.getItemCooldownManager().set(this, 280);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
