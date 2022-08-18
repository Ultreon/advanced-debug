package com.ultreon.mods.advanceddebug.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class DebugRenderContext implements IDebugRenderContext {
    private int left;
    private int top;
    private int right;

    private final PoseStack pose;
    private final Minecraft mc = Minecraft.getInstance();
    private final int width;
    private final int height;
    private static final int HORIZONTAL_OFFSET = 6;
    private static final int VERTICAL_OFFSET = 6;

    public DebugRenderContext(PoseStack pose, int width, int height) {
        this.pose = pose;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void left(Component text, Object object, Object... objects) {
        drawLeft(pose, text.getString(), left++, object, objects);
    }

    @Override
    public void left(String text, Object object, Object... objects) {
        drawLeft(pose, text, left++, object, objects);
    }

    @Override
    public void left(Component text) {
        drawLeft(pose, text.getString(), left++);
    }

    @Override
    public void left(String text) {
        drawLeft(pose, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", left++);
    }

    @Override
    public void left() {
        left++;
    }

    @Override
    public void right(Component text, Object object, Object... objects) {
        drawRight(pose, text.getString(), right++, object, objects);
    }

    @Override
    public void right(String text, Object object, Object... objects) {
        drawRight(pose, text, right++, object, objects);
    }

    @Override
    public void right(Component text) {
        drawRight(pose, text.getString(), right++);
    }

    @Override
    public void right(String text) {
        drawRight(pose, ChatFormatting.GRAY + "-== " + text + ChatFormatting.GRAY + " ==-", right++);
    }

    @Override
    public void right() {
        right++;
    }

    @Override
    public void top(Component text) {
        drawTop(pose, text.getString(), top++);
    }

    @Override
    public void top(String text) {
        drawTop(pose, text, top++);
    }

    @Override
    public void top() {
        top++;
    }

    private void drawTop(PoseStack pose, String text, int line) {
        drawLine(pose, new TextComponent(text), (int) (this.width / 2f - mc.font.width(text) / 2f), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawLeft(PoseStack pose, String text, int line, Object obj, Object... objects) {
        drawLine(pose, DebugGui.get().format(text, obj, objects), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawLeft(PoseStack pose, String text, int line) {
        drawLine(pose, new TextComponent(text), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawRight(PoseStack pose, String text, int line, Object obj, Object... objects) {
        Component format = DebugGui.get().format(text, obj, objects);
        drawLine(pose, format, this.width - HORIZONTAL_OFFSET - mc.font.width(format), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    private void drawRight(PoseStack pose, String text, int line) {
        drawLine(pose, new TextComponent(text), this.width - HORIZONTAL_OFFSET - mc.font.width(text), VERTICAL_OFFSET + line * (mc.font.lineHeight + 2));
    }

    protected abstract void drawLine(PoseStack pose, Component text, int x, int y);
}
