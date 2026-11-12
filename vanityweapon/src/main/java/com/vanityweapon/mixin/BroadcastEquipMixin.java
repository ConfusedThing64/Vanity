package com.vanityweapon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.vanityweapon.VanityData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.objectweb.asm.Opcodes;

import java.util.UUID;

/**
 * Soulbound's TurnBattleMod.broadcastEquipInfo() tells every battle participant which weapon each
 * player has equipped (this is what other players render). We swap the value that gets BROADCAST
 * for the vanity item. The real PlayerProgress.equippedWeapon is never modified, so stats/damage stay.
 */
@Mixin(targets = "com.pixelcite.turnbattle.TurnBattleMod")
public abstract class BroadcastEquipMixin {
    @ModifyExpressionValue(
            method = "broadcastEquipInfo",
            at = @At(value = "FIELD",
                    target = "Lcom/pixelcite/turnbattle/BattleData$PlayerProgress;equippedWeapon:Lnet/minecraft/class_1799;",
                    opcode = Opcodes.GETFIELD, remap = false),
            remap = false)
    private static ItemStack vanityweapon$swap(ItemStack real,
                                               @Local(argsOnly = true) MinecraftServer server,
                                               @Local UUID participant) {
        return VanityData.override(real, VanityData.get(server, participant));
    }
}
