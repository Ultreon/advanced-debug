package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.IntSize;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ComputerPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();
    private final Window window = mc.getWindow();

    public ComputerPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        Monitor monitor = window.findBestMonitor();

        if (monitor != null) {
            long l = GLFW.glfwGetWindowMonitor(window.getWindow());
            VideoMode currentMode = monitor.getCurrentMode();
            ctx.left("Monitor");
            ctx.left("Screen Size", new IntSize(currentMode.getWidth(), currentMode.getHeight()));
            ctx.left("Screen Pos", new IntSize(monitor.getX(), monitor.getY()));
            ctx.left("Refresh Rate", currentMode.getRefreshRate());
            ctx.left("RGB Bits", currentMode.getRedBits(), currentMode.getGreenBits(), currentMode.getBlueBits());
            ctx.left("Bits", currentMode.getRedBits() + currentMode.getGreenBits() + currentMode.getBlueBits());
            ctx.left();
        }

        ctx.left("System Info");
        try {
            ctx.left("OS Version", System.getProperty("os.version"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("OS Name", System.getProperty("os.name"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("OS Architecture", System.getProperty("os.arch"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java Version", System.getProperty("java.version"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java Vendor", System.getProperty("java.vendor"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java VM Version", System.getProperty("java.vm.version"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java VM Vendor", System.getProperty("java.vm.vendor"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java VM Name", System.getProperty("java.vm.name"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java Class Version", System.getProperty("java.class.version"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }
        try {
            ctx.left("Java Compiler", System.getProperty("java.compiler"));
        } catch (SecurityException | IllegalArgumentException | NullPointerException ignored) {

        }

        ctx.right("Is Java 64-bit", (mc.is64Bit() ? "yes" : "no"));
    }
}
