package com.vanityweapon.client;

import com.vanityweapon.SetVanityPayload;
import com.vanityweapon.VanitySyncPayload;
import com.vanityweapon.mixin.client.EquipScreenAccess;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class VanityClient implements ClientModInitializer {
    private static final String EQUIP_SCREEN = "com.pixelcite.turnbattle.EquipScreen";
    /** Soulbound's sockets are 28x28 (half size 14); ours matches. */
    private static final int HALF = 14;
    private static final int GAP = 12;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(VanitySyncPayload.ID,
                (payload, ctx) -> ClientVanity.stack = payload.stack());
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> ClientVanity.stack = ItemStack.EMPTY);

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!screen.getClass().getName().equals(EQUIP_SCREEN)) return;

            ScreenEvents.afterRender(screen).register((s, ctx, mx, my, delta) -> renderBox(s, ctx, mx, my));

            ScreenMouseEvents.allowMouseClick(screen).register((s, mx, my, button) -> {
                int[] c = boxCentre(s);
                if (c == null || !inside(mx, my, c)) return true;
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                if (button == 1) {
                    ClientPlayNetworking.send(new SetVanityPayload(-1)); // right-click clears
                } else if (button == 0) {
                    mc.setScreen(new VanityPickScreen(s));
                }
                return false; // consume the click so Soulbound's screen doesn't also react
            });
        });
    }

    /** Centre of our box (left of the weapon socket), or null if hidden (party member selected / not laid out yet). */
    private static int[] boxCentre(Screen s) {
        EquipScreenAccess acc = (EquipScreenAccess) (Object) s;
        if (acc.vanityweapon$selected() != -1) return null; // vanity is for YOUR character only
        float[][] sp = acc.vanityweapon$socketPos();
        if (sp == null || sp.length == 0) return null;
        float wx = sp[0][0], wy = sp[0][1];
        if (wx == 0f && wy == 0f) return null;
        return new int[]{(int) wx - HALF * 2 - GAP, (int) wy};
    }

    private static boolean inside(double mx, double my, int[] c) {
        return mx >= c[0] - HALF && mx < c[0] + HALF && my >= c[1] - HALF && my < c[1] + HALF;
    }

    private static void renderBox(Screen s, DrawContext ctx, int mx, int my) {
        int[] c = boxCentre(s);
        if (c == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean hover = inside(mx, my, c);
        ItemStack v = ClientVanity.stack;
        boolean has = v != null && !v.isEmpty();

        int x0 = c[0] - HALF, y0 = c[1] - HALF, x1 = c[0] + HALF, y1 = c[1] + HALF;
        int border = hover ? 0xFFFFFF55 : has ? 0xFFE04040 : 0xFF6A2020;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, 0, 300);
        ctx.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, border);
        ctx.fill(x0, y0, x1, y1, 0xF0050A12);

        if (has) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(c[0], c[1], 0);
            ctx.getMatrices().scale(1.5f, 1.5f, 1f);
            ctx.drawItem(v, -8, -8);
            ctx.getMatrices().pop();
        } else {
            ctx.drawCenteredTextWithShadow(mc.textRenderer, "V", c[0], c[1] - 4, 0xFF804040);
        }
        ctx.drawCenteredTextWithShadow(mc.textRenderer, "VANITY", c[0], y1 + 6, hover ? 0xFFFF55 : 0xFFAAAAAA);

        if (hover) {
            ctx.drawTooltip(mc.textRenderer, has
                    ? List.of(v.getName(), Text.literal("Click: change   Right-click: clear"))
                    : List.of(Text.literal("Vanity Weapon"), Text.literal("Click to pick any item from your inventory.")),
                    mx, my);
        }
        ctx.getMatrices().pop();
    }
}
