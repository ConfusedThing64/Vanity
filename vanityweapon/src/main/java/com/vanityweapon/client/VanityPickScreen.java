package com.vanityweapon.client;

import com.vanityweapon.SetVanityPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Simple inventory picker: click any item to use it as your vanity weapon. */
public class VanityPickScreen extends Screen {
    private static final int CELL = 22;
    private final Screen parent;

    public VanityPickScreen(Screen parent) {
        super(Text.literal("Vanity Weapon"));
        this.parent = parent;
    }

    private int gridLeft() { return (width - 9 * CELL) / 2; }
    private int gridTop() { return height / 2 - 62; }

    /** Visual layout: main inventory (slots 9-35) on top, hotbar (0-8) below a small gap. */
    private int slotAt(double mx, double my) {
        int col = (int) Math.floor((mx - gridLeft()) / CELL);
        if (col < 0 || col > 8) return -1;
        int row = (int) Math.floor((my - gridTop()) / CELL);
        if (row >= 0 && row < 3) return 9 + row * 9 + col;
        double hy = my - (gridTop() + 3 * CELL + 6);
        if (hy >= 0 && hy < CELL) return col;
        return -1;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int by = gridTop() + 4 * CELL + 16;
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear vanity"), b -> choose(-1))
                .dimensions(cx - 102, by, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close())
                .dimensions(cx + 2, by, 100, 20).build());
    }

    private void choose(int slot) {
        ClientPlayNetworking.send(new SetVanityPayload(slot));
        close();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        // Plain dim; avoids vanilla's blur (which may only be applied once per frame).
        ctx.fill(0, 0, width, height, 0xD0000000);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        ctx.drawCenteredTextWithShadow(textRenderer, "Pick a vanity weapon", width / 2, gridTop() - 30, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "Looks only. Your equipped weapon's stats are unchanged.",
                width / 2, gridTop() - 18, 0xAAAAAA);

        int hover = slotAt(mx, my);
        ItemStack cur = ClientVanity.stack;
        for (int s = 0; s < 36; s++) {
            int x, y;
            if (s >= 9) { x = gridLeft() + ((s - 9) % 9) * CELL; y = gridTop() + ((s - 9) / 9) * CELL; }
            else { x = gridLeft() + s * CELL; y = gridTop() + 3 * CELL + 6; }
            ItemStack st = mc.player.getInventory().getStack(s);
            boolean current = !st.isEmpty() && !cur.isEmpty() && st.getItem() == cur.getItem();
            int border = (s == hover) ? 0xFFFFFF55 : current ? 0xFFE04040 : 0xFF404040;
            ctx.fill(x, y, x + CELL - 2, y + CELL - 2, border);
            ctx.fill(x + 1, y + 1, x + CELL - 3, y + CELL - 3, 0xFF101018);
            if (!st.isEmpty()) {
                ctx.drawItem(st, x + 2, y + 2);
                ctx.drawItemInSlot(textRenderer, st, x + 2, y + 2);
            }
        }
        if (hover >= 0) {
            ItemStack st = mc.player.getInventory().getStack(hover);
            if (!st.isEmpty()) ctx.drawItemTooltip(textRenderer, st, mx, my);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        if (button == 0) {
            int s = slotAt(mx, my);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (s >= 0 && mc.player != null && !mc.player.getInventory().getStack(s).isEmpty()) {
                choose(s);
                return true;
            }
        }
        return false;
    }
}
