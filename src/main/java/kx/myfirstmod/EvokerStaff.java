package kx.myfirstmod;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class EvokerStaff extends Item {
    public EvokerStaff(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Ensure we don't spawn the lightning only on the client.
        // This is to prevent desync.
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        castSpell(user);
        user.getItemCooldownManager().set(this, 35);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    protected void castSpell(PlayerEntity user) {
        double dist = 5;
        Vec3d lookDir = user.getRotationVector();
        Vec3d lookDirHorizontal = (new Vec3d(lookDir.x, 0, lookDir.z)).normalize();
        Vec3d target = lookDirHorizontal.multiply(dist).add(user.getPos());
        double d = Math.min(target.getY(), user.getY()) - 5;
        double e = Math.max(target.getY(), user.getY()) + (double)1.0F * 5;
        float f = (float)MathHelper.atan2(target.getZ() - user.getZ(), target.getX() - user.getX());
        if (Math.abs(user.getPitch()) > 85 || user.isSneaking()) {
            //radial fangs
            for(int i = 0; i < 5; ++i) {
                float g = f + (float)i * (float)Math.PI * 0.4F;
                this.conjureFangs(user, user.getX() + (double)MathHelper.cos(g) * (double)1.5F, user.getZ() + (double)MathHelper.sin(g) * (double)1.5F, d, e, g, 0);
            }

            for(int i = 0; i < 8; ++i) {
                float g = f + (float)i * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                this.conjureFangs(user, user.getX() + (double)MathHelper.cos(g) * (double)2.5F, user.getZ() + (double)MathHelper.sin(g) * (double)2.5F, d, e, g, 3);
            }

        } else {
            //directional fangs
            for(int i = 0; i < 16; ++i) {
                double h = (double)1.25F * (double)(i + 1);
                int j = 1 * i;
                this.conjureFangs(user, user.getX() + (double)MathHelper.cos(f) * h, user.getZ() + (double)MathHelper.sin(f) * h, d, e, f, j);
            }
        }

    }

    private void conjureFangs(PlayerEntity user, double x, double z, double maxY, double y, float yaw, int warmup) {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        boolean bl = false;
        double d = (double)0.0F;

        do {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = user.getWorld().getBlockState(blockPos2);
            if (blockState.isSideSolidFullSquare(user.getWorld(), blockPos2, Direction.UP)) {
                if (!user.getWorld().isAir(blockPos)) {
                    BlockState blockState2 = user.getWorld().getBlockState(blockPos);
                    VoxelShape voxelShape = blockState2.getCollisionShape(user.getWorld(), blockPos);
                    if (!voxelShape.isEmpty()) {
                        d = voxelShape.getMax(Direction.Axis.Y);
                    }
                }

                bl = true;
                break;
            }

            blockPos = blockPos.down();
        } while(blockPos.getY() >= MathHelper.floor(maxY) - 1);

        if (bl) {
            user.getWorld().spawnEntity(new EvokerFangsEntity(user.getWorld(), x, (double)blockPos.getY() + d, z, yaw, warmup, user));
        }

    }
}