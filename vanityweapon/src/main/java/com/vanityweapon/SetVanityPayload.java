package com.vanityweapon;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Client -> server: use the item in this main-inventory slot (0-35) as vanity weapon, or -1 to clear. */
public record SetVanityPayload(int slot) implements CustomPayload {
    public static final Id<SetVanityPayload> ID = new Id<>(Identifier.of("vanityweapon", "set"));
    public static final PacketCodec<RegistryByteBuf, SetVanityPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, SetVanityPayload::slot, SetVanityPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
