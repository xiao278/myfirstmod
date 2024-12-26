package kx.myfirstmod.items;

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
        Entity target = EntityDetector.findClosestCrosshairEntity(world, user, 20, 45);
        if (target == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        Direction.Axis initialDir = user.getPitch() <= -45 ? Direction.UP.getAxis() : user.getHorizontalFacing().getAxis();
        world.spawnEntity(new ShulkerBulletEntity(world, user, target, initialDir));
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
