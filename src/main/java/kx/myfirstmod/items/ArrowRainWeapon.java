package kx.myfirstmod.items;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArrowRainWeapon extends Item {
    public ArrowRainWeapon(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        world.spawnEntity(new ArrowEntity(world, user.getX(), user.getY() + 5, user.getZ()));
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private void spawnArrows(World world, BlockPos coordinates, PlayerEntity user) {
        world.spawnEntity(new ArrowEntity(world, coordinates.getX(), coordinates.getY(), coordinates.getZ()));
    }
}
