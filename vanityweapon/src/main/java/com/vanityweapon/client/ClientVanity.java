package com.vanityweapon.client;

import net.minecraft.item.ItemStack;

/** The local player's own vanity stack, as synced by the server. */
public final class ClientVanity {
    private ClientVanity() {}

    public static volatile ItemStack stack = ItemStack.EMPTY;

    /** For render-only call sites: vanity if set, otherwise the real equipped weapon. */
    public static ItemStack swap(ItemStack real) {
        ItemStack v = stack;
        return (v == null || v.isEmpty()) ? real : v;
    }
}
