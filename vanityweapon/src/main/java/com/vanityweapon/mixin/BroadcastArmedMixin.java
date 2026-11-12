package com.vanityweapon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vanityweapon.VanityData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Same idea for Live Combat: LiveCombat.broadcastArmed() tells nearby clients what weapon you draw. */
@Mixin(targets = "com.pixelcite.turnbattle.LiveCombat")
public abstract class BroadcastArmedMixin {
    @ModifyExpressionValue(
            method = "broadcastArmed",
            at = @At(value = "FIELD",
                    target = "Lcom/pixelcite/turnbattle/BattleData$PlayerProgress;equippedWeapon:Lnet/minecraft/class_1799;",
                    opcode = Opcodes.GETFIELD, remap = false),
            remap = false)
    private static ItemStack vanityweapon$swap(ItemStack real,
                                               @Local(argsOnly = true) ServerPlayerEntity player) {
        return VanityData.override(real, VanityData.get(player));
    }
}
