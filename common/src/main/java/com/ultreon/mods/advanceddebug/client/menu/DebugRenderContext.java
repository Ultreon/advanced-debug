package com.ultreon.mods.advanceddebug.client.menu;

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

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
        return this.guiGraphics;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public void left(Component text, Object object, Object... objects) {
        this.drawLeft(this.guiGraphics, text.getString(), this.left++, object, objects);
    }

    @Override
    public void left(String text, Object object, Object... objects) {
        this.drawLeft(this.guiGraphics, text, this.left++, object, objects);
    }

    @Override
    public void left(Component text) {
        this.drawLeft(this.guiGraphics, text.getString(), this.left++);
    }

    @Override
    public void left(String text) {
        this.drawLeft(this.guiGraphics, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", this.left++);
    }

    @Override
    public void left() {
        this.left++;
    }

    @Override
    public void right(Component text, Object object, Object... objects) {
        this.drawRight(this.guiGraphics, text.getString(), this.right++, object, objects);
    }

    @Override
    public void right(String text, Object object, Object... objects) {
        this.drawRight(this.guiGraphics, text, this.right++, object, objects);
    }

    @Override
    public void right(Component text) {
        this.drawRight(this.guiGraphics, text.getString(), this.right++);
    }

    @Override
    public void right(String text) {
        this.drawRight(this.guiGraphics, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", this.right++);
    }

    @Override
    public void right() {
        this.right++;
    }

    @Override
    public void top(Component text) {
        this.drawTop(this.guiGraphics, text.getString(), this.top++);
    }

    @Override
    public void top(String text) {
        this.drawTop(this.guiGraphics, text, this.top++);
    }

    @Override
    public void top() {
        this.top++;
    }

    private void drawTop(@NotNull GuiGraphics gfx, String text, int line) {
        this.drawLine(gfx, Component.literal(text), (int) (this.width / 2f - this.mc.font.width(text) / 2f), VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawTop(@NotNull GuiGraphics gfx, Component text, int line) {
        this.drawLine(gfx, text, (int) (this.width / 2f - this.mc.font.width(text) / 2f), VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawLeft(@NotNull GuiGraphics gfx, String text, int line, Object obj, Object... objects) {
        this.drawLine(gfx, DebugGui.get().format(text, obj, objects), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawLeft(@NotNull GuiGraphics gfx, String text, int line) {
        this.drawLine(gfx, Component.literal(text), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawLeft(@NotNull GuiGraphics gfx, Component text, int line) {
        this.drawLine(gfx, text, HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawRight(@NotNull GuiGraphics gfx, String text, int line, Object obj, Object... objects) {
        Component format = DebugGui.get().format(text, obj, objects);
        this.drawLine(gfx, format, this.width - HORIZONTAL_OFFSET - this.mc.font.width(format), VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawRight(@NotNull GuiGraphics gfx, String text, int line) {
        this.drawLine(gfx, Component.literal(text), this.width - HORIZONTAL_OFFSET - this.mc.font.width(text), VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    private void drawRight(@NotNull GuiGraphics gfx, Component text, int line) {
        this.drawLine(gfx, text, this.width - HORIZONTAL_OFFSET - this.mc.font.width(text), VERTICAL_OFFSET + line * (this.mc.font.lineHeight + 2));
    }

    protected abstract void drawLine(@NotNull GuiGraphics gfx, Component text, int x, int y);

    public void left(GuiGraphics gfx, MutableComponent component) {
        this.drawLeft(gfx, component, this.left++);
    }

    public void right(GuiGraphics gfx, MutableComponent component) {
        this.drawRight(gfx, component, this.right++);
    }

    public void top(GuiGraphics gfx, MutableComponent component) {
        this.drawRight(gfx, component, this.top++);
    }
}
