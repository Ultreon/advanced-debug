package com.ultreon.mods.advanceddebug.client.menu.pages;

import com.google.common.collect.Lists;
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
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.PhysicalMemory;

import java.util.Arrays;
import java.util.List;

public class ComputerPage extends DebugPage {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private final Minecraft mc = Minecraft.getInstance();

    public ComputerPage() {
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        long l = GLFW.glfwGetWindowMonitor(getMainWindow().getWindow());
        Monitor monitor = ((WindowAccessor)(Object) getMainWindow()).getScreenManager().getMonitor(l);
        long l = GLFW.glfwGetWindowMonitor(getWindow().getWindow());
        Monitor monitor = ((WindowAccessor)(Object)getWindow()).getScreenManager().getMonitor(l);

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

        ctx.right("System");
        ctx.right("Is Java 64-bit", (minecraft.is64Bit() ? "yes" : "no"));
        ctx.right("Platform", SystemInfo.getCurrentPlatform());

        CentralProcessor processor = SYSTEM_INFO.getHardware().getProcessor();
        ctx.right("Processor");
//        ctx.right("CPU Frequencies", Arrays.stream(processor.getCurrentFreq()).boxed().toList());
        ctx.right("CPU Max Frequency", Lists.newArrayList(processor.getMaxFreq()));
        ctx.right("CPU Phys. Processor Count", processor.getPhysicalProcessorCount());
        ctx.right("CPU Logic. Processor Count", processor.getLogicalProcessorCount());
        ctx.right("CPU Interrupts", processor.getInterrupts());
        ctx.right("CPU Load", processor.getProcessorCpuLoad(1000000000L));

        GlobalMemory memory = SYSTEM_INFO.getHardware().getMemory();
        ctx.right("Memory");
        ctx.right("Physical Memory", memory.getPhysicalMemory().stream().mapToLong(PhysicalMemory::getCapacity).sum());
        ctx.right("Virtual Memory", memory.getVirtualMemory().getVirtualMax());
        ctx.right("Swap Memory", memory.getVirtualMemory().getSwapTotal());
        ctx.right("Total Memory", memory.getTotal());
        ctx.right("Memory Available", memory.getAvailable());
    }
}
