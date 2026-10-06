package com.havoc.fpsdisplay;

import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class FPSDisplayClient implements ClientModInitializer {
    public static final String MOD_ID = "fpsdisplay";

    private static final KeyBinding OPEN_MENU = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "key.fpsdisplay.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            KeyBinding.Category.MISC
        )
    );

    private static boolean enabled = false;
    private static int hudX = 12;
    private static int hudY = 12;

    private static int boxWidth(MinecraftClient client) {
        return Math.max(78, client.textRenderer.getWidth("FPS: " + client.getCurrentFps()) + 18);
    }

    private static void clampPosition(MinecraftClient client) {
        int maxX = Math.max(0, client.getWindow().getScaledWidth() - boxWidth(client));
        int maxY = Math.max(0, client.getWindow().getScaledHeight() - 24);
        hudX = Math.max(0, Math.min(hudX, maxX));
        hudY = Math.max(0, Math.min(hudY, maxY));
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.wasPressed()) {
                client.setScreen(new FPSDisplayScreen());
            }
        });

        HudElementRegistry.addLast(
            Identifier.of(MOD_ID, "fps_overlay"),
            (DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) -> {
                if (!enabled) return;
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == null || client.options.hudHidden) return;

                clampPosition(client);
                String text = "FPS: " + client.getCurrentFps();
                int width = boxWidth(client);
                int height = 24;

                context.fill(hudX, hudY, hudX + width, hudY + height, 0xCC000000);
                context.fill(hudX, hudY, hudX + width, hudY + 1, 0xFFFFFFFF);
                context.fill(hudX, hudY + height - 1, hudX + width, hudY + height, 0xFFFFFFFF);
                context.fill(hudX, hudY, hudX + 1, hudY + height, 0xFFFFFFFF);
                context.fill(hudX + width - 1, hudY, hudX + width, hudY + height, 0xFFFFFFFF);
                context.drawText(client.textRenderer, text, hudX + 9, hudY + 8, 0xFFFFFFFF, false);
            }
        );
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static int getHudX() {
        return hudX;
    }

    public static int getHudY() {
        return hudY;
    }

    public static void setHudPosition(int x, int y) {
        hudX = x;
        hudY = y;
        MinecraftClient client = MinecraftClient.getInstance();
        clampPosition(client);
    }
}
