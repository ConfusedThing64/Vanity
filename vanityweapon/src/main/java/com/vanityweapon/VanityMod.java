package com.vanityweapon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class VanityMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SetVanityPayload.ID, SetVanityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VanitySyncPayload.ID, VanitySyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetVanityPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            int slot = payload.slot();
            if (slot < 0) {
                player.removeAttached(VanityData.VANITY);
            } else if (slot < 36) {
                // Server-side validation: the item must really be in the player's inventory.
                ItemStack inInv = player.getInventory().getStack(slot);
                if (inInv.isEmpty()) return;
                player.setAttached(VanityData.VANITY, inInv.copyWithCount(1));
            } else {
                return;
            }
            sync(player);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.getPlayer()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> sync(newP));
    }

    public static void sync(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new VanitySyncPayload(VanityData.get(player).copy()));
    }
}
