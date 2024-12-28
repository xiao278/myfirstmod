package kx.myfirstmod.items;

import kx.myfirstmod.utils.BinomialDistribution;
import kx.myfirstmod.utils.BlockDetector;
import kx.myfirstmod.utils.SummonLightning;
import kx.myfirstmod.utils.TaskScheduler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
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

        // everything onwards here on out is serverside only
        BlockPos blockPos = BlockDetector.getBlockLookingAt(world, user, 50);
        if (blockPos == null) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        // Spawn the lightning bolt.
        // SummonLightning.summon(world, blockPos, 0, true);

        int size = 7;
        int strikes = 6;
        int iters = 2;
        if (EnchantmentHelper.getLevel(Enchantments.SWEEPING, user.getStackInHand(hand)) > 0) {
            size += 7;
            iters += 1;
        }
        if (EnchantmentHelper.getLevel(Enchantments.SMITE, user.getStackInHand(hand)) > 0) {
            strikes += 3;
        }
        for (int strike = 0; strike < strikes; strike++) {
            for (int i = 0; i < iters; i++) {
                int modX = BinomialDistribution.sample(size - 1, 0.5) - (size / 2);
                int modZ = BinomialDistribution.sample(size - 1, 0.5)  - (size / 2);
                Runnable runnable = SummonLightning.getRunnable(
                        world,
                        new BlockPos(blockPos.getX() + modX, blockPos.getY(), blockPos.getZ() + modZ),
                        5,
                        true
                );
                TaskScheduler.schedule(runnable, strike);
            }
        };

        // Nothing has changed to the item stack,
        // so we just return it how it was.
        user.getItemCooldownManager().set(this, 60);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
