package com.vanityweapon;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Server -> client: your own current vanity stack (empty = none). */
public record VanitySyncPayload(ItemStack stack) implements CustomPayload {
    public static final Id<VanitySyncPayload> ID = new Id<>(Identifier.of("vanityweapon", "sync"));
    public static final PacketCodec<RegistryByteBuf, VanitySyncPayload> CODEC =
            PacketCodec.tuple(ItemStack.OPTIONAL_PACKET_CODEC, VanitySyncPayload::stack, VanitySyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
