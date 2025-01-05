package kx.myfirstmod.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class GuardianLaserDamageSource extends DamageSource {

    public GuardianLaserDamageSource(RegistryEntry<DamageType> type, @Nullable Entity attacker) {
        super(type, attacker);
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        if (entity != null && this.getAttacker() != null) {
            // Example: Custom death message
            return Text.translatable("death.attack.guardian_core", entity.getDisplayName(), this.getAttacker().getDisplayName());
        }
        return super.getDeathMessage(entity);
    }
}
