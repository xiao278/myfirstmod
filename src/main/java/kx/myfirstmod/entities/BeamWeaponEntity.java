package kx.myfirstmod.entities;

import kx.myfirstmod.enchantments.ModEnchantments;
import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.misc.GuardianLaserDamageSource;
import kx.myfirstmod.particles.HelicalParticleEffect;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeamWeaponEntity extends ProjectileEntity {
    private final int BASE_BEAM_TICKS = 18;
    private float piercingDamageProportion;
    private float baseDamage;
    private float baseDOT;
    private float maxRange;
    private static final TrackedData<Float> BEAM_LENGTH;
    private static final TrackedData<Integer> PROJECTILE_SPECIALIZATION;
    private static final TrackedData<Integer> BEAM_TICKS;
    public static enum ProjectileSpecialization {
        NONE, POWER, PIERCE, LONGSHOT
    }

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BeamWeaponEntity(EntityType<? extends ProjectileEntity> entityType, World world, ItemStack stack, float beamLength) {
        super(entityType, world);
        this.dataTracker.set(BEAM_LENGTH, beamLength);
        this.setProjectileSpecialization(stack);
        this.piercingDamageProportion = BeamWeapon.getMagicDamageProportion(stack);
        this.maxRange = BeamWeapon.getMaxRange(stack);
        this.baseDamage = BeamWeapon.getDamage(stack);
        this.baseDOT = BeamWeapon.getLingeringDamage(stack);
        this.dataTracker.set(BEAM_TICKS, BeamWeapon.DEBUG_MODE ? 100 : (
                this.getProjectileSpecialization() == ProjectileSpecialization.LONGSHOT ?
                        (int) (BASE_BEAM_TICKS * 1.5) :
                        BASE_BEAM_TICKS
                )
        );
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BEAM_LENGTH, 0f);
        this.dataTracker.startTracking(BEAM_TICKS, BASE_BEAM_TICKS);
        this.dataTracker.startTracking(PROJECTILE_SPECIALIZATION, 0);
    }

    static {
        BEAM_LENGTH = DataTracker.registerData(BeamWeaponEntity.class, TrackedDataHandlerRegistry.FLOAT);
        BEAM_TICKS = DataTracker.registerData(BeamWeaponEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PROJECTILE_SPECIALIZATION = DataTracker.registerData(BeamWeaponEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    public float getBeamLength() {return this.dataTracker.get(BEAM_LENGTH);}

    @Override
    public void tick() {
        super.tick();
        World world = this.getWorld();
        if (world.isClient) {
            if (getProjectileSpecialization() == ProjectileSpecialization.LONGSHOT) {
                if (this.getBeamTicks() - this.age < 2) {
                    Vec3d vel = this.getVelocity().multiply(1/10d);
                    for (int i = 0; i < getBeamLength(); i++) {
                        Vec3d pos = this.getPos().add(this.getVelocity().multiply(random.nextDouble() * getBeamLength()));
                        world.addParticle(new HelicalParticleEffect(0.3,0.2), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
                    }
                }
                this.setYaw((float)(MathHelper.atan2(this.getVelocity().x, this.getVelocity().z) * (180F / Math.PI)));
                this.setPitch((float)(MathHelper.atan2(this.getVelocity().y, Math.sqrt(this.getVelocity().x * this.getVelocity().x + this.getVelocity().z * this.getVelocity().z)) * (180F / Math.PI)));
            }
        }
        else {
            if (getProjectileSpecialization() == ProjectileSpecialization.LONGSHOT && this.getOwner() instanceof PlayerEntity) {
                PlayerEntity owner = (PlayerEntity) this.getOwner();
                Vec3d o = BeamWeapon.getShootOrigin(owner);
                this.setPos(o.x, o.y, o.z);
                this.setVelocity(owner.getRotationVector());
                dataTracker.set(BEAM_LENGTH, BeamWeapon.calcBeamLength(world, owner, this.getPos(), this.getVelocity(), maxRange));
            }
            if (this.age == 1 || (getProjectileSpecialization() == ProjectileSpecialization.LONGSHOT)) {
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
                if (this.getProjectileSpecialization() == ProjectileSpecialization.LONGSHOT) {
                    this.baseDamage = baseDOT / this.getBeamTicks();
                    this.piercingDamageProportion = 1;
                }
            }
            if (this.age > getBeamTicks()) this.discard();
        }
    }

    public int getBeamTicks() {
        return this.dataTracker.get(BEAM_TICKS);
    }

    private float calcBeamTickDamage(float totalDOT) {
        return totalDOT / this.getBeamTicks();
    }

    private void setProjectileSpecialization(ItemStack stack) {
        ProjectileSpecialization ps = ProjectileSpecialization.NONE;
        if (EnchantmentHelper.getLevel(Enchantments.POWER, stack) > 0) ps = ProjectileSpecialization.POWER;
        if (EnchantmentHelper.getLevel(Enchantments.PIERCING, stack) > 0) ps = ProjectileSpecialization.PIERCE;
        if (EnchantmentHelper.getLevel(ModEnchantments.LONG_SHOT, stack) > 0) ps = ProjectileSpecialization.LONGSHOT;
        this.dataTracker.set(PROJECTILE_SPECIALIZATION, ps.ordinal());
    }

    public ProjectileSpecialization getProjectileSpecialization() {
        int ordinal_value = this.dataTracker.get(PROJECTILE_SPECIALIZATION);
        ProjectileSpecialization[] value_list = ProjectileSpecialization.values();
        if (ordinal_value < 0 || ordinal_value >= value_list.length) return ProjectileSpecialization.NONE;
        return value_list[ordinal_value];
    }
}
