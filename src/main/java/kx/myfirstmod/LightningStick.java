package kx.myfirstmod;


import org.apache.commons.math3.distribution.BinomialDistribution;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

        BlockPos blockPos = BlockDetector.getBlockLookingAt(world, user, 75);
        if (blockPos == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        // Spawn the lightning bolt.
        // SummonLightning.summon(world, blockPos, 0, true);

        int size = 7;
        BinomialDistribution bd = new BinomialDistribution(size - 1, 0.5);
        for (int delay = 0; delay < 6; delay++) {
            for (int iter = 0; iter < 2; iter++) {
                int modX = bd.sample() - (size / 2);
                int modZ = bd.sample() - (size / 2);
                Runnable runnable = SummonLightning.getRunnable(
                        world,
                        new BlockPos(blockPos.getX() + modX, blockPos.getY(), blockPos.getZ() + modZ),
                        5,
                        true
                );
                TaskScheduler.schedule(runnable, delay);
            }
        };

        // Nothing has changed to the item stack,
        // so we just return it how it was.
        user.getItemCooldownManager().set(this, 60);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
