package kx.myfirstmod;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.MultishotEnchantment;
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
        // serverside stuff
        castSpell(user, hand);
        user.getItemCooldownManager().set(this, 35);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    protected void castSpell(PlayerEntity user, Hand hand) {
        double detect_range = 35;
        LivingEntity target = EntityDetector.findClosestCrosshairEntity(user.getWorld(), user, detect_range, 45);
        Vec3d targetPos = target == null ? user.getPos() : target.getPos();
        Vec3d lookDir = user.getRotationVector();
        Vec3d lookDirHorizontal = (new Vec3d(lookDir.x, 0, lookDir.z)).normalize();
        Vec3d targetDir = lookDirHorizontal.add(user.getPos());
        double d = Math.min(targetPos.getY(), user.getY());
        double e = Math.max(targetPos.getY(), user.getY()) + (double)1.0F;
        float f = (float)MathHelper.atan2(targetDir.getZ() - user.getZ(), targetDir.getX() - user.getX());
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

            int[] lvl_fangs = {11,14,17,20};
            for (int lvl = 0; lvl < EnchantmentHelper.getLevel(Enchantments.PIERCING, user.getStackInHand(hand)); lvl++) {
                float circumference = ((float) lvl_fangs[lvl] / 8.0F) * 2.5F;
                for (int i = 0; i < lvl_fangs[lvl]; i++) {
                    float g = f + (float) (Math.PI * 2.0F) * i / lvl_fangs[lvl] + lvl * 1.2566371F;
                    this.conjureFangs(user, user.getX() + (double)MathHelper.cos(g) * (double)circumference, user.getZ() + (double)MathHelper.sin(g) * (double)circumference, d, e, g, (lvl + 2) * 3);
                }
            }

        } else {
            //directional fangs
            for(int i = 0; i < 16 + EnchantmentHelper.getLevel(Enchantments.PIERCING, user.getStackInHand(hand)) * 4;
                ++i) {
                double h = (double)1.25F * (double)(i + 1);
                int j = 1 * i;
                this.conjureFangs(user, user.getX() + (double)MathHelper.cos(f) * h, user.getZ() + (double)MathHelper.sin(f) * h, d, e, f, j);
            }
            if (EnchantmentHelper.getLevel(Enchantments.MULTISHOT, user.getStackInHand(hand)) > 0) {
                for(int i = 0; i < 16; ++i) {
                    double h = (double)1.25F * (double)(i + 1);
                    int j = 1 * i;
                    float spread_angle = (float) 8 / 180 * MathHelper.PI;
                    float f_right = f + spread_angle;
                    float f_left = f - spread_angle;
                    float orthog_angle = (float) MathHelper.PI / 2;
                    float g_right = f + orthog_angle;
                    float g_left = f - orthog_angle;
                    this.conjureFangs(user, user.getX() + (double)MathHelper.cos(f_right) * h + MathHelper.cos(g_right), user.getZ() + (double)MathHelper.sin(f_right) * h + MathHelper.sin(g_right), d, e, f_right, j);
                    this.conjureFangs(user, user.getX() + (double)MathHelper.cos(f_left) * h + MathHelper.cos(g_left), user.getZ() + (double)MathHelper.sin(f_left) * h + MathHelper.sin(g_left), d, e, f_left, j);
                }
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
