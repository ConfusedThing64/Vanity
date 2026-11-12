package com.vanityweapon.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vanityweapon.client.ClientVanity;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Render-only: shows the vanity item wherever OmegaCinema.equippedWeapon reads the player's own equipped weapon. */
@Mixin(targets = "com.pixelcite.turnbattle.OmegaCinema")
public abstract class OmegaCinemaMixin {
    @ModifyExpressionValue(
            method = "equippedWeapon",
            at = @At(value = "FIELD",
                    target = "Lcom/pixelcite/turnbattle/TurnBattleClientMod;clientEquippedWeapon:Lnet/minecraft/class_1799;",
                    opcode = Opcodes.GETSTATIC, remap = false),
            remap = false)
    private static ItemStack vanityweapon$swap(ItemStack real) {
        return ClientVanity.swap(real);
    }
}
