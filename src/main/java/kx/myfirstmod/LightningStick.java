package kx.myfirstmod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightningStick extends Item {
    public LightningStick(Item.Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        BlockPos blockPos = BlockDetector.getBlockLookingAt(world, user, 20);
        if (blockPos == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        // Spawn the lightning bolt.
        SummonLightning.summon(world, blockPos, 0);
        Runnable runnable = SummonLightning.getRunnable(world, blockPos, 0);

        TaskScheduler.schedule(runnable, 10);
        TaskScheduler.schedule(runnable, 20);


        // Nothing has changed to the item stack,
        // so we just return it how it was.
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
