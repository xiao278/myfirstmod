package kx.myfirstmod;

import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class GuardianLaserEntity extends ProjectileEntity {
    private static final TrackedData<Integer> TARGET_ID;
    private static final TrackedData<Optional<UUID>> TARGET_UUID;
    private LivingEntity target = null;
    private int beamTicks;

    public GuardianLaserEntity(EntityType<? extends ProjectileEntity> entityType, World world, LivingEntity target, PlayerEntity caster) {
        super(entityType, world);
        this.target = target;
        this.setOwner(caster);
        this.beamTicks = 0;
        if (!this.getWorld().isClient() && this.target != null) {
            this.dataTracker.set(TARGET_ID, this.target.getId());
            this.dataTracker.set(TARGET_UUID, Optional.ofNullable(this.target.getUuid()));
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
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        World world = this.getWorld();
        if (world.isClient()) {
            System.out.println("18273");
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
        return 80;
    }

    public float getBeamTicks() {
        return (float)this.beamTicks;
    }


    public float getBeamProgress(float tickDelta) {
        return ((float)this.beamTicks + tickDelta) / (float)this.getWarmupTime();
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
        if (this.getWorld().isClient()) {
            if (!this.hasBeamTarget()) {
//                System.out.printf("{this: %s, target: %s} discarded%n", this.toString(), this.target);
//                System.out.println(this.getPos());
            }
            if (this.isRemoved()) return;
            Random random = world.getRandom();
            for (int i = 0; i < 3; i++) {
                world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        this.getX() + random.nextDouble() * 0.2,
                        this.getY() + random.nextDouble() * 0.2 + 1,
                        this.getZ() + random.nextDouble() * 0.2,
                        0, 0, 0
                );
            }
        } else {
            if (this.age > 500 || !this.hasBeamTarget()) {
                this.discard();
                return;
            }
            Vec3d targetPos = null;
            if (this.target == null) targetPos = new Vec3d(0,0,0);
            else targetPos = target.getPos();
            this.setPosition(targetPos);
        }
        this.beamTicks++;
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
