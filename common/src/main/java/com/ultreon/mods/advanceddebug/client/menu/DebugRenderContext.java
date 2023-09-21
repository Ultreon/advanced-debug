package com.ultreon.mods.advanceddebug.client.menu;

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class DebugRenderContext implements IDebugRenderContext {
    private int left;
    private int top;
    private int right;

    private final GuiGraphics guiGraphics;
    private final Minecraft mc = Minecraft.getInstance();
    private final int width;
    private final int height;
    private static final int HORIZONTAL_OFFSET = 6;
    private static final int VERTICAL_OFFSET = 6;

    public DebugRenderContext(@NotNull GuiGraphics gfx, int width, int height) {
        this.guiGraphics = gfx;
        this.width = width;
        this.height = height;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void left(Component text, Object object, Object... objects) {
        drawLeft(guiGraphics, text.getString(), left++, object, objects);
    }

    @Override
    public void left(String text, Object object, Object... objects) {
        drawLeft(guiGraphics, text, left++, object, objects);
    }

    @Override
    public void left(Component text) {
        drawLeft(guiGraphics, text.getString(), left++);
    }

    @Override
    public void left(String text) {
        drawLeft(guiGraphics, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", left++);
    }

    @Override
    public void left() {
        left++;
    }

    @Override
    public void right(Component text, Object object, Object... objects) {
        drawRight(guiGraphics, text.getString(), right++, object, objects);
    }

    @Override
    public void right(String text, Object object, Object... objects) {
        drawRight(guiGraphics, text, right++, object, objects);
    }

    @Override
    public void right(Component text) {
        drawRight(guiGraphics, text.getString(), right++);
    }

    @Override
    public void right(String text) {
        drawRight(guiGraphics, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", right++);
    }

    @Override
    public void right() {
        right++;
    }

    @Override
    public void top(Component text) {
        drawTop(guiGraphics, text.getString(), top++);
    }

    @Override
    public void top(String text) {
        drawTop(guiGraphics, text, top++);
    }

    @Override
    public void top() {
        top++;
    }

    private void drawTop(@NotNull GuiGraphics gfx, String text, int line) {
        drawLine(gfx, Component.literal(text), (int) (this.width / 2f - mc.font.width(text) / 2f), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawLeft(@NotNull GuiGraphics gfx, String text, int line, Object obj, Object... objects) {
        drawLine(gfx, DebugGui.get().format(text, obj, objects), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawLeft(@NotNull GuiGraphics gfx, String text, int line) {
        drawLine(gfx, Component.literal(text), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawRight(@NotNull GuiGraphics gfx, String text, int line, Object obj, Object... objects) {
        Component format = DebugGui.get().format(text, obj, objects);
        drawLine(gfx, format, this.width - HORIZONTAL_OFFSET - mc.font.width(format), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawRight(@NotNull GuiGraphics gfx, String text, int line) {
        drawLine(gfx, Component.literal(text), this.width - HORIZONTAL_OFFSET - mc.font.width(text), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    protected abstract void drawLine(@NotNull GuiGraphics gfx, Component text, int x, int y);
}
