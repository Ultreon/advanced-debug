package com.ultreon.mods.advanceddebug.client.menu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.common.*;
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

    public abstract void render(PoseStack poseStack, DebugRenderContext ctx);

    protected static Formattable getFormatted(String s) {
        return () -> s;
    }

    protected static Formattable getMultiplier(double multiplier) {
        return new Multiplier(multiplier);
    }

    protected static Formattable getSize(int w, int h) {
        return new IntSize(w, h);
    }

    protected static Formattable getSize(float w, float h) {
        return new FloatSize(w, h);
    }

    protected static Formattable getPercentage(double value) {
        return new Percentage(value);
    }

    protected static Color getColor(Vec3 color) {
        return new Color((float) color.x, (float) color.y, (float) color.z);
    }

    protected static Color getColor(int rgb) {
        return new Color(rgb);
    }

    protected static Formattable getAngle(double angle) {
        return new Angle(angle * 360.0d);
    }

    protected static Formattable getRadians(double angle) {
        return new Angle(Math.toDegrees(angle));
    }

    protected static Formattable getDegrees(double angle) {
        return new Angle(angle);
    }

    protected static MoonPhase getMoonPhase(int index) {
        return MoonPhase.fromIndex(index);
    }

    public Minecraft getMinecraft() {
        return mc;
    }

    public ResourceLocation registryName() {
        return resourceLocation;
    }
}
