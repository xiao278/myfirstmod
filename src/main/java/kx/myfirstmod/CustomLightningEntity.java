package kx.myfirstmod;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.List;

public class CustomLightningEntity extends LightningEntity {
    private boolean cosmetic;
    private int blocksSetOnFire;

    public CustomLightningEntity(EntityType<? extends LightningEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age == 1) {
            this.getStruckEntities().forEach(e -> {
                // retrieve vanilla lightning damage
                if (e.getType() == this.getType()) return;
//                System.out.println(e.getName());
//                RegistryEntry<DamageType> lightningBoltDamage = e.getWorld().getRegistryManager()
//                        .get(RegistryKeys.DAMAGE_TYPE)
//                        .entryOf(DamageTypes.LIGHTNING_BOLT);
                e.timeUntilRegen = 0;
            });
            this.discard();
        }
        spawnFire(4);
    }


    private void spawnFire(int spreadAttempts) {
        if (!this.cosmetic && !this.getWorld().isClient && this.getWorld().getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            BlockPos blockPos = this.getBlockPos();
            BlockState blockState = AbstractFireBlock.getState(this.getWorld(), blockPos);
            if (this.getWorld().getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos)) {
                this.getWorld().setBlockState(blockPos, blockState);
                ++this.blocksSetOnFire;
            }

            for(int i = 0; i < spreadAttempts; ++i) {
                BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
                blockState = AbstractFireBlock.getState(this.getWorld(), blockPos2);
                if (this.getWorld().getBlockState(blockPos2).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos2)) {
                    this.getWorld().setBlockState(blockPos2, blockState);
                    ++this.blocksSetOnFire;
                }
            }

        }
    }
}
