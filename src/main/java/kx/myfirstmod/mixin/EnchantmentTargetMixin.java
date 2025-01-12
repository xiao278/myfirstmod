package kx.myfirstmod.mixin;

import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.EvokerStaff;
import kx.myfirstmod.items.GuardianLaser;
import kx.myfirstmod.items.LightningStick;
import net.minecraft.enchantment.*;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentTargetMixin {
    @Shadow @Nullable protected String translationKey;

    @Shadow public abstract String getTranslationKey();

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void injectGuardianLaserEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // have guardian laser accept enchantmnet
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof GuardianLaser &&
                (self instanceof QuickChargeEnchantment || self instanceof PowerEnchantment)
        ) {
            cir.setReturnValue(true); // Allow the enchantment
        }
    }

    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    protected void injectGuardianLaserCombine(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        Class<?>[] mutex_enchs = {PowerEnchantment.class, QuickChargeEnchantment.class};
        Enchantment self = (Enchantment) (Object) this;
        boolean compatible = isCompatible(self, other, mutex_enchs);
        if (!compatible) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void injectEvokerStaffEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // have guardian laser accept enchantmnet
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof EvokerStaff &&
                (self instanceof MultishotEnchantment || self instanceof PiercingEnchantment)
        ) {
            cir.setReturnValue(true); // Allow the enchantment
        }
    }

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void injectLightningStickEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // have guardian laser accept enchantmnet
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof LightningStick &&
                (self instanceof SweepingEnchantment || self.getTranslationKey().equals("enchantment.minecraft.smite"))
        ) {
            cir.setReturnValue(true); // Allow the enchantment
        }
    }

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void injectBeamWeaponEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // have guardian laser accept enchantmnet
        Enchantment self = (Enchantment) (Object) this;
        if (stack.getItem() instanceof BeamWeapon &&
                (self instanceof PowerEnchantment || self instanceof PiercingEnchantment)
        ) {
            cir.setReturnValue(true); // Allow the enchantment
        }
    }

    @Unique
    private boolean isCompatible(Enchantment a, Enchantment b, Class<?>[] mutex_ench) {
        boolean a_in = false;
        boolean b_in = false;
        boolean a_is_b = a.getTranslationKey().equals(b.getTranslationKey());
        if (a_is_b) return false;
        for (Class<?> ench: mutex_ench) {
            a_in = a_in || ench.isInstance(a);
            b_in = b_in || ench.isInstance(b);
        }
        return !(a_in && b_in);
    }
}