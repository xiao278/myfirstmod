package kx.myfirstmod.items;

import kx.myfirstmod.MyFirstMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> {
            itemGroup.add(ModItems.LIGHTNING_STICK);
            itemGroup.add(ModItems.GUARDIAN_LASER);
            itemGroup.add(ModItems.EVOKER_STAFF);
            itemGroup.add(ModItems.SHULKER_STAFF);
            itemGroup.add(ModItems.ARROW_RAIN);
            itemGroup.add(ModItems.BEAM_WEAPON);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            ItemStack creativeGem = new ItemStack(ModItems.EFFECT_GEM);
            EffectGem.storeIsCreative(creativeGem, true);
            itemGroup.add(creativeGem);
        });
    }

    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(MyFirstMod.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

    public static final Item LIGHTNING_STICK = register(
            new LightningStick(new Item.Settings()
                    .maxCount(1)
                    .fireproof()
            ),
            "lightning_stick"
    );

    public static final Item GUARDIAN_LASER = register(
            new GuardianLaser(new Item.Settings().maxCount(1)),
            "guardian_core"
    );

    public static final Item EVOKER_STAFF = register(
            new EvokerStaff(new Item.Settings()),
            "evoker_staff"
    );

    public static final Item SHULKER_STAFF = register (
            new ShulkerStaff(new Item.Settings().maxCount(1)),
            "shulker_staff"
    );

    public static final Item ARROW_RAIN = register (
            new ArrowRainWeapon(new Item.Settings().maxCount(1)),
            "arrow_rain"
    );

    public static final Item EFFECT_GEM = register(
            new EffectGem(new Item.Settings().maxCount(1)),
            "effect_gem"
    );

    public static final Item BEAM_WEAPON = register(
            new BeamWeapon(new Item.Settings().maxCount(1)),
            "beam_weapon"
    );
}
