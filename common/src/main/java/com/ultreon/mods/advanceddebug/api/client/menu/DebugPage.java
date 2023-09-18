package com.ultreon.mods.advanceddebug.api.client.menu;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.api.common.*;
import com.ultreon.mods.advanceddebug.registry.ModPreRegistries;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

@SuppressWarnings("unused")
public abstract class DebugPage {
    protected final Minecraft minecraft = Minecraft.getInstance();
    private final ResourceLocation resourceLocation;
    protected final Minecraft mc = Minecraft.getInstance();

    public DebugPage() {
        // Mod container.
        Mod mod = Platform.getMod(modId);
        if (mod == null) {
            throw new IllegalArgumentException("Mod not found with id: " + modId);
        }
        this.resourceLocation = new ResourceLocation(modId, name);
    }

    protected final Window getMainWindow() {
        return mc.getWindow();
    }

    public abstract void render(PoseStack poseStack, IDebugRenderContext ctx);

    @Deprecated(forRemoval = true, since = "1.2.0")
    protected static IFormattable getFormatted(String s) {
        return new IFormattable() {
            @SuppressWarnings("removal")
            @Override
            public String toFormattedString() {
                return s;
            }
        };
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
        return minecraft;
    }

    public Identifier getId() {
        Identifier id = ModPreRegistries.DEBUG_PAGE.getKey(this);
        if (id == null) throw new IllegalStateException("Debug page not registered: " + getClass().getName());
        Mod mod = Platform.getMod(id.location());
        if (mod == null) throw new IllegalArgumentException("Mod not found with id: " + id.location());
        return id;
    }

    protected Window getWindow() {
        return this.minecraft.getWindow();
    }
}
