package com.havoc.fpsdisplay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class FPSDisplayScreen extends Screen {
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    public FPSDisplayScreen() {
        super(Text.literal("FPS Display"));
    }

    private int panelX() {
        return this.width - 220;
    }

    private int panelY() {
        return 10;
    }

    private boolean inButton(double x, double y) {
        int px = panelX();
        return x >= px + 12 && x <= px + 208 && y >= panelY() + 42 && y <= panelY() + 72;
    }

    private boolean inHud(double x, double y) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = Math.max(78, client.textRenderer.getWidth("FPS: " + client.getCurrentFps()) + 18);
        int h = 24;
        int hx = FPSDisplayClient.getHudX();
        int hy = FPSDisplayClient.getHudY();
        return x >= hx && x <= hx + w && y >= hy && y <= hy + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int px = panelX();
        int py = panelY();
        int pw = 210;
        int ph = 112;

        context.fill(px, py, px + pw, py + ph, 0xD9000000);
        context.fill(px, py, px + pw, py + 2, 0xFFFFFFFF);
        context.drawText(this.textRenderer, "FPS DISPLAY", px + 12, py + 12, 0xFFFFFFFF, false);

        boolean on = FPSDisplayClient.isEnabled();
        int buttonColor = on ? 0xFF2EAE55 : 0xFF3A3A3A;
        context.fill(px + 12, py + 42, px + 208, py + 72, buttonColor);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("FPS DISPLAY"), px + 110, py + 52, 0xFFFFFFFF);

        context.drawText(this.textRenderer, "Enabled: " + (on ? "YES" : "NO"), px + 12, py + 82, on ? 0xFF55FF55 : 0xFFFF5555, false);
        context.drawText(this.textRenderer, "Drag the FPS box to move it.", px + 12, py + 96, 0xFFB0B0B0, false);

        if (on) {
            MinecraftClient client = MinecraftClient.getInstance();
            String text = "FPS: " + client.getCurrentFps();
            int width = Math.max(78, this.textRenderer.getWidth(text) + 18);
            int height = 24;
            int x = FPSDisplayClient.getHudX();
            int y = FPSDisplayClient.getHudY();
            context.fill(x, y, x + width, y + height, 0xCC000000);
            context.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
            context.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            context.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
            context.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF);
            context.drawText(this.textRenderer, text, x + 9, y + 8, 0xFFFFFFFF, false);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double x = click.x();
        double y = click.y();

        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (inButton(x, y)) {
                FPSDisplayClient.toggle();
                return true;
            }
            if (FPSDisplayClient.isEnabled() && inHud(x, y)) {
                dragging = true;
                dragOffsetX = (int) x - FPSDisplayClient.getHudX();
                dragOffsetY = (int) y - FPSDisplayClient.getHudY();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging && click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            FPSDisplayClient.setHudPosition((int) click.x() - dragOffsetX, (int) click.y() - dragOffsetY);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            dragging = false;
        }
        return super.mouseReleased(click);
    }
}
