package com.ultreon.mods.advanceddebug.api.client.menu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.common.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.awt.*;

@SuppressWarnings("unused")
public abstract class DebugPage {
    protected final Minecraft mc = Minecraft.getInstance();
    protected final Window mainWindow;
    private final ResourceLocation resourceLocation;

    public DebugPage(String modId, String name) {
        // Mod container.
        ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalArgumentException("Mod not found with id: " + modId));
        this.mainWindow = this.mc.getWindow();
        this.resourceLocation = new ResourceLocation(modId, name);
    }

    public abstract void render(PoseStack poseStack, IDebugRenderContext ctx);

    protected static IFormattable getFormatted(String s) {
        return () -> s;
    }

    protected static IFormattable getMultiplier(double multiplier) {
        return new Multiplier(multiplier);
    }

    protected static IFormattable getSize(int w, int h) {
        return new IntSize(w, h);
    }

    protected static IFormattable getSize(float w, float h) {
        return new FloatSize(w, h);
    }

    protected static IFormattable getPercentage(double value) {
        return new Percentage(value);
    }

    protected static Color getColor(Vec3 color) {
        return new Color((float) color.x, (float) color.y, (float) color.z);
    }

    protected static Color getColor(int rgb) {
        return new Color(rgb);
    }

    protected static IFormattable getAngle(double angle) {
        return new Angle(angle * 360.0d);
    }

    protected static IFormattable getRadians(double angle) {
        return new Angle(Math.toDegrees(angle));
    }

    protected static IFormattable getDegrees(double angle) {
        return new Angle(angle);
    }

    public Minecraft getMinecraft() {
        return mc;
    }

    public ResourceLocation registryName() {
        return resourceLocation;
    }
}
