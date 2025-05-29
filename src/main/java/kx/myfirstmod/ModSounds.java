package kx.myfirstmod;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModSounds {
    public static final SoundEvent GUARDIAN_LASER_CHARGE_SOUND = registerSound("guardian_attack_loop_custom");
    public static final SoundEvent WEAPON_BEAM_FIRE = registerSound("weapon_beam_fire");
    public static final SoundEvent WEAPON_BEAM_FIRE_LONGSHOT = registerSound("weapon_beam_fire_longshot");
    public static final SoundEvent WEAPON_BEAM_READY = registerSound("weapon_beam_fully_charged");
    public static final SoundEvent WEAPON_BEAM_CHARGE_SOUND = registerSound("weapon_beam_charge_loop");


    private static SoundEvent registerSound(String name) {
        Identifier id = new Identifier(MyFirstMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
        // Register sounds here
    }
}
