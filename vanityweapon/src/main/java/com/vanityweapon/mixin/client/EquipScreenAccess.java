package com.vanityweapon.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only access to Soulbound's EquipScreen internals (so we can place the box next to the weapon socket). */
@Mixin(targets = "com.pixelcite.turnbattle.EquipScreen")
public interface EquipScreenAccess {
    /** socketPos[0] = {x, y} centre of the weapon socket, refreshed every frame. */
    @Accessor(value = "socketPos", remap = false)
    float[][] vanityweapon$socketPos();

    /** Selected roster member code; -1 means "you". */
    @Accessor(value = "selected", remap = false)
    int vanityweapon$selected();
}
