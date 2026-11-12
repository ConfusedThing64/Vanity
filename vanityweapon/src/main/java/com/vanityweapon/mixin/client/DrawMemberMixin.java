package com.vanityweapon.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vanityweapon.client.ClientVanity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * VoidMenuScreen.drawMember() draws the 3D character preview (Equip/Stats/Party screens).
 * When no explicit preview stack is passed it asks weaponOf(code). For the player (code -1) we
 * return the vanity item so the preview model matches what you'll look like in battle.
 */
@Mixin(targets = "com.pixelcite.turnbattle.VoidMenuScreen")
public abstract class DrawMemberMixin {
    @WrapOperation(
            method = "drawMember",
            at = @At(value = "INVOKE",
                    target = "Lcom/pixelcite/turnbattle/VoidMenuScreen;weaponOf(I)Lnet/minecraft/class_1799;",
                    remap = false),
            remap = false)
    private ItemStack vanityweapon$preview(Screen self, int code, Operation<ItemStack> original) {
        ItemStack real = original.call(self, code);
        return code == -1 ? ClientVanity.swap(real) : real;
    }
}
