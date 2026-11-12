package com.vanityweapon;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public final class VanityData {
    private VanityData() {}

    /** Persistent per-player vanity stack, saved with the player and kept across death. */
    public static final AttachmentType<ItemStack> VANITY = AttachmentRegistry.<ItemStack>builder()
            .persistent(ItemStack.OPTIONAL_CODEC)
            .copyOnDeath()
            .buildAndRegister(Identifier.of("vanityweapon", "vanity_weapon"));

    public static ItemStack get(ServerPlayerEntity player) {
        if (player == null) return ItemStack.EMPTY;
        ItemStack s = player.getAttached(VANITY);
        return s == null ? ItemStack.EMPTY : s;
    }

    public static ItemStack get(net.minecraft.server.MinecraftServer server, UUID id) {
        if (server == null || id == null) return ItemStack.EMPTY;
        return get(server.getPlayerManager().getPlayer(id));
    }

    /** Returns the vanity stack if the player has one, otherwise the original (real) weapon. */
    public static ItemStack override(ItemStack real, ItemStack vanity) {
        return (vanity == null || vanity.isEmpty()) ? real : vanity;
    }
}
