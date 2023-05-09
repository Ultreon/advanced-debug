package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.IntSize;
import com.ultreon.mods.advanceddebug.mixin.common.WindowAccessor;
import com.ultreon.mods.advanceddebug.util.FileSize;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ComputerPage extends DebugPage {
    private final Minecraft mc = Minecraft.getInstance();

    public ComputerPage() {
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        long l = GLFW.glfwGetWindowMonitor(getMainWindow().getWindow());
        Monitor monitor = ((WindowAccessor)(Object) getMainWindow()).getScreenManager().getMonitor(l);

        if (monitor != null) {
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

        ctx.right("Total RAM", new FileSize(Runtime.getRuntime().totalMemory()));
        ctx.right("Max RAM", new FileSize(Runtime.getRuntime().maxMemory()));
        ctx.right("Free RAM", new FileSize(Runtime.getRuntime().freeMemory()));
    }
}
