package kx.myfirstmod.mixin;

import kx.myfirstmod.GuardianLaser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.QuickChargeEnchantment;
import net.minecraft.item.Item;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentTargetMixin {
    @Shadow @Nullable protected String translationKey;

    @Shadow public abstract String getTranslationKey();

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void injectCustomItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Example: Extend WEAPON to include your custom weapon
        if (stack.getItem() instanceof GuardianLaser && translationKey != null && translationKey.equals("enchantment.minecraft.quick_charge")) {
            cir.setReturnValue(true); // Allow the enchantment
        }
    }
}