package kx.myfirstmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class GuardianLaserEntity extends ProjectileEntity {
    private static final TrackedData<Integer> TARGET_ID;
    private static final TrackedData<Optional<UUID>> TARGET_UUID;
    private static final TrackedData<Integer> WARMUP_TIME;
    private float damage = 0;
    private LivingEntity target = null;
    private int beamTicks;

    public GuardianLaserEntity(EntityType<? extends ProjectileEntity> entityType, World world, LivingEntity target, PlayerEntity caster, float damage, int warmup_time) {
        super(entityType, world);
        this.target = target;
        if (!this.getWorld().isClient() && this.target != null) {
            this.setOwner(caster);
            this.damage = damage;
            this.beamTicks = 0;
            this.dataTracker.set(TARGET_ID, this.target.getId());
            this.dataTracker.set(TARGET_UUID, Optional.ofNullable(this.target.getUuid()));
            this.dataTracker.set(WARMUP_TIME, warmup_time);
        }
    }

    @Override
    public PlayerEntity getOwner() {
        Entity player = super.getOwner();
        if (player != null) assert player instanceof PlayerEntity;
        return (PlayerEntity) player;
    }

    public GuardianLaserEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.beamTicks = 0;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TARGET_ID, -1);
        this.dataTracker.startTracking(TARGET_UUID, Optional.empty());
        this.dataTracker.startTracking(WARMUP_TIME, GuardianLaser.getMaxWarmupTime());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        World world = this.getWorld();
        if (world.isClient()) {
//            System.out.println("18273");
        }
        if (data.equals(TARGET_ID)) {
            int targetId = this.dataTracker.get(TARGET_ID);
            if (targetId >= 0) {
                Entity maybeEntity = world.getEntityById(targetId);
                if (maybeEntity instanceof LivingEntity) {
                    this.target = (LivingEntity) maybeEntity;
                }
            }
        }
    }

    static {
        TARGET_ID = DataTracker.registerData(GuardianLaserEntity.class, TrackedDataHandlerRegistry.INTEGER);
        TARGET_UUID = DataTracker.registerData(GuardianLaserEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        WARMUP_TIME = DataTracker.registerData(GuardianLaserEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

//    @Override
//    protected void writeCustomDataToNbt(NbtCompound nbt) {
//        super.writeCustomDataToNbt(nbt);
//        if (this.target != null) {
////            System.out.printf("written to nbt {target: %s}\n", target.toString());
//            nbt.putInt("targetId", this.target.getId());
//            nbt.putUuid("targetUuid", this.target.getUuid());
//        }
//    }

    public int getWarmupTime() {
        return this.dataTracker.get(WARMUP_TIME);
    }

    public float getBeamTicks() {
        return (float)this.beamTicks;
    }


    public float getBeamProgress(float tickDelta) {
        return Math.min(((float)this.beamTicks + tickDelta) / (float)this.getWarmupTime(), (float) 1.01);
    }

//    @Override
//    protected void readCustomDataFromNbt(NbtCompound nbt) {
//        super.readCustomDataFromNbt(nbt);
//        if (nbt.contains("targetId") && nbt.contains("targetUuid")) {
//            int targetId = nbt.getInt("targetID");
//            UUID targetUuid = nbt.getUuid("targetUuid");
//            Entity maybeTarget = this.getWorld().getEntityById(targetId);
//            // on re-logging there will be error fetching target
//            if (maybeTarget != null && maybeTarget.getUuid() == targetUuid && maybeTarget instanceof LivingEntity) {
//                this.target = (LivingEntity) maybeTarget;
//                this.targetId = targetId;
//                this.targetUuid = targetUuid;
//            }
//            else {
//                System.out.printf("GuardianLaserEntity %s: Error fetching target\n", this);
//            }
//        }
//    }

    public boolean hasBeamTarget() {
        // equivalent to not null target AND not removed AND alive
        return !(this.target == null || this.target.isRemoved() || !this.target.isAlive());
    }

    public LivingEntity getBeamTarget() {
        return this.target;
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.getWorld();
        if (world.isClient()) {
            if (!this.hasBeamTarget()) {
            }
            if (this.isRemoved()) return;
        } else {
            if (this.age > 200 || !this.hasBeamTarget() || getOwner() == null || getOwner().isRemoved() || !getOwner().isAlive()) {
                this.discard();
                return;
            }
            if (getOwner() != null) {
                // check if caster still using guardian core
                PlayerEntity player = getOwner();
                ItemStack selectedItem = player.getInventory().getStack(player.getInventory().selectedSlot);
                if (!selectedItem.isOf(ModItems.GUARDIAN_LASER)) {
                    this.stopUsing();
                }
                // follow caster around
                if (getOwner().getPos().distanceTo(this.getPos()) > 8) {
                    this.setPosition(getOwner().getPos());
                    this.setVelocity(new Vec3d(0,0,0));
                }
                // check if caster has a clear Line of Sight
                if (!EntityDetector.isLineOfSightClear(world, player, target)) {
                    player.stopUsingItem();
                }
            }
        }
        if (beamTicks <= this.getWarmupTime()) this.beamTicks++;
    }

    public void stopUsing() {
        if (!this.getWorld().isClient()){
            if (hasBeamTarget() && this.getBeamTicks() >= this.getWarmupTime()) {
                RegistryEntry<DamageType> dtype = this.getWorld().getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC);
                target.damage(new DamageSource(dtype, this.getOwner()), damage);
            }
            this.discard();
        }
    }

    @Override
    public boolean isPushable() {
        return false; // Prevent being pushed by other entities
    }

    @Override
    public boolean canHit() {
        return false; // Prevent the entity from being hit
    }
}
