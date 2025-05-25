package kx.myfirstmod.entities;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.misc.GuardianLaserDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeamWeaponEntity extends ProjectileEntity {
    public final int LIVING_TICKS = BeamWeapon.DEBUG_MODE ? 100 : 18;
    private float piercingDamageProportion;
    private float baseDamage;
    private static final TrackedData<Float> BEAM_LENGTH;

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world, Vec3d pos, Vec3d vel, float baseDamage, float piercingDamageProportion, float beamLength) {
        super(entityType, world);
        this.setVelocity(vel);
        this.setPosition(pos);
        this.piercingDamageProportion = piercingDamageProportion;
        this.baseDamage = baseDamage;
        this.dataTracker.set(BEAM_LENGTH, beamLength);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BEAM_LENGTH, 0f);
    }

    static {
        BEAM_LENGTH = DataTracker.registerData(BeamWeaponEntity.class, TrackedDataHandlerRegistry.FLOAT);
    }

    public float getBeamLength() {return this.dataTracker.get(BEAM_LENGTH);}

    @Override
    public void tick() {
        super.tick();
        World world = this.getWorld();
        if (world.isClient) {

        }
        else {
            if (this.age == 1) {
                double lerp_progress = 0;
                Vec3d origin = this.getPos();
                Vec3d dir = this.getVelocity();
                Set<LivingEntity> hitEntities = new HashSet<>();
                while (lerp_progress < this.dataTracker.get(BEAM_LENGTH)) {
                    Vec3d lerp_pos = origin.add(dir.multiply(lerp_progress));
                    Box searchBox = new Box(lerp_pos.subtract(BeamWeapon.BEAM_WIDTH, BeamWeapon.BEAM_WIDTH, BeamWeapon.BEAM_WIDTH), lerp_pos.add(BeamWeapon.BEAM_WIDTH, BeamWeapon.BEAM_WIDTH, BeamWeapon.BEAM_WIDTH));
                    List<Entity> potentialTargets = world.getOtherEntities(this.getOwner(), searchBox, entity -> (entity instanceof PlayerEntity || entity instanceof MobEntity));
                    for (Entity e : potentialTargets) {
                        if (e instanceof LivingEntity livingEntity) {
                            hitEntities.add(livingEntity);
                        }
                    }
                    lerp_progress += BeamWeapon.BEAM_WIDTH;
                }
                RegistryEntry<DamageType> dtypeNonPierce = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.PLAYER_ATTACK);
                RegistryEntry<DamageType> dtypePierce = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC);
                for (LivingEntity e : hitEntities) {
                    Vec3d prev_vel = e.getVelocity();
                    e.damage(new GuardianLaserDamageSource(dtypeNonPierce, this.getOwner()), baseDamage  * (1 - piercingDamageProportion));
                    e.timeUntilRegen = 0;
                    e.damage(new GuardianLaserDamageSource(dtypePierce, this.getOwner()), baseDamage * piercingDamageProportion);
                    e.timeUntilRegen = 0;
                    e.setVelocity(prev_vel);
                }
            }
            if (this.age > LIVING_TICKS) this.discard();
        }
    }
}
