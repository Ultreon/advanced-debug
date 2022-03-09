package com.ultreon.mods.advanceddebug.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.common.*;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList;
import com.ultreon.mods.advanceddebug.client.menu.pages.DefaultPage;
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry;
import com.ultreon.mods.advanceddebug.util.InputUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.util.FastColor.ARGB32.*;

/**
 * Client listener
 *
 * @author (partial) CoFH - https://github.com/CoFH
 */
@SuppressWarnings("unused")
public final class DebugGui implements IIngameOverlay, IDebugGui {
    private static final DebugGui INSTANCE = new DebugGui();
    private static final List<DebugPage> pages = new ArrayList<>();
    private static final DebugPage DEFAULT = new DefaultPage();
    private static final FormatterRegistry FORMATTER_REGISTRY = FormatterRegistry.get();
    private static final Formatter<Object> DEFAULT_FORMATTER = new Formatter<>(Object.class, AdvancedDebug.res("object")) {
        @Override
        public void format(Object obj, StringBuilder sb) {
            Class<?> c = obj.getClass();
            sb.append(AQUA);
            sb.append(c.getPackage().getName().replaceAll("\\.", GRAY + "." + AQUA));
            sb.append(GRAY).append(".").append(AQUA);
            sb.append(ChatFormatting.DARK_AQUA);
            sb.append(c.getSimpleName());
            sb.append(GRAY).append("@").append(YELLOW);
            sb.append(Integer.toHexString(obj.hashCode()));
        }
    };
    private static final Map<ResourceLocation, DebugPage> PAGE_REGISTRY = new HashMap<>();
    private Font font;
    private int page = 0;

    private DebugGui() {
        
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack pose, float partialTicks, int width, int height) {
        if (Minecraft.getInstance().options.renderDebug) {
            return;
        }

        font = Minecraft.getInstance().font;

        DebugPage debugPage = getDebugPage();
        Minecraft mc = Minecraft.getInstance();

        DebugRenderContext context = new DebugRenderContext(pose, width, height) {
            @Override
            protected void drawLine(PoseStack pose, String text, int x, int y) {
                DebugGui.this.drawLine(pose, text, x, y);
            }
        };

        if (debugPage != null) {
            drawLine(pose, "Debug Page: " + debugPage.registryName().toString(), 6, height - 16);
            debugPage.render(pose, context);
        } else {
            DEFAULT.render(pose, context);
        }
    }

    private void drawLine(PoseStack pose, String text, int x, int y) {
        Screen.fill(pose, x, y, x + font.width(text) + 2, (y - 1) + font.lineHeight + 2, 0x5f000000);
        font.draw(pose, text, x + 1, y + 1, 0xffffff);
    }

    public static DebugGui get() {
        return INSTANCE;
    }

    public List<DebugPage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    @SuppressWarnings("UnusedReturnValue")
    public <T extends DebugPage> T registerPage(T page) {
        pages.add(page);
        if (PAGE_REGISTRY.containsKey(page.registryName())) {
            throw new IllegalArgumentException("Duplicate debug page registration: " + page.registryName());
        }
        PAGE_REGISTRY.put(page.registryName(), page);
        return page;
    }

    @Override
    @Nullable
    public DebugPage getDebugPage() {
        fixPage();
        if (page == 0) {
            return null;
        }
        return pages.get(page - 1);
    }

    private void fixPage() {
        page %= pages.size() + 1;
        if (page < 0) {
            page += pages.size() + 1;
        }
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page % (pages.size() + 1);
    }

    @Override
    public void setPage(DebugPage page) {
        if (!pages.contains(page)) {
            throw new IllegalArgumentException("Page not registered.");
        }
        this.page = pages.indexOf(page);
    }

    @Override
    public void next() {
        setPage(getPage() + 1);
    }

    @Override
    public void prev() {
        setPage(getPage() - 1);
    }

    public void onKeyReleased(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_RELEASE && event.getKey() == KeyBindingList.DEBUG_SCREEN.getKey().getValue()) {
            if (InputUtils.isShiftDown()) prev();
            else next();
        }
    }

    private IFormattable getFormatted(String s) {
        return () -> s;
    }

    private IFormattable getMultiplier(double multiplier) {
        return new Multiplier(multiplier);
    }

    private IFormattable getSize(int w, int h) {
        return new IntSize(w, h);
    }

    private IFormattable getSize(float w, float h) {
        return new FloatSize(w, h);
    }

    private IFormattable getPercentage(double value) {
        return new Percentage(value);
    }

    private Color getColor(Vec3 color) {
        return new Color((float) color.x, (float) color.y, (float) color.z);
    }

    private Color getColor(int rgb) {
        return new Color(red(rgb), green(rgb), blue(rgb), alpha(rgb));
    }

    private IFormattable getAngle(double angle) {
        return new Angle(angle * 360.0d);
    }

    private IFormattable getRadians(double angle) {
        return new Angle(Math.toDegrees(angle));
    }

    private IFormattable getDegrees(double angle) {
        return new Angle(angle);
    }

    public String format(String text, Object obj, Object... objects) {
        StringBuilder sb = new StringBuilder();

//        sb.append(ChatFormatting.DARK_AQUA).append(text);
        sb.append(WHITE).append(ITALIC).append(text);
        sb.append(GRAY).append(": ");
        sb.append(format(obj));

        for (Object object : objects) {
            sb.append(GRAY).append(", ");
            sb.append(format(object));
        }

        return sb.toString();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String format(Object obj) {
        StringBuilder sb = new StringBuilder();

        if (obj == null) {
            sb.append(LIGHT_PURPLE);
            sb.append("null");
        } else if (obj instanceof Class<?> c) {
            sb.append(AQUA);
            sb.append(c.getPackage().getName().replaceAll("\\.", GRAY + "." + AQUA));
            sb.append(GRAY).append(".").append(AQUA);
            sb.append(ChatFormatting.DARK_AQUA);
            sb.append(c.getSimpleName());
        } else {
            Formatter identified = FORMATTER_REGISTRY.identify(obj.getClass());
            if (identified != null) {
                identified.format(obj, sb);
                return sb.toString();
            }

            DEFAULT_FORMATTER.format(obj, sb);
        }
        return sb.toString();
    }

    private void drawTopString(PoseStack matrixStack, String text, int line) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        // Draw text.
        drawLine(matrixStack, text, (int) (width / 2f - fontRenderer.width(text) / 2f), (int) (12f + (line * 12)));
    }

    private void drawLeftTopString(PoseStack matrixStack, String text, int line, Object obj, Object... objects) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        text = format(text, obj, objects);

        // Draw text.
        drawLine(matrixStack, text, (int) 12f, (int) (12f + (line * 12)));
    }

    private void drawRightTopString(PoseStack matrixStack, String text, int line, Object obj, Object... objects) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        text = format(text, obj, objects);

        // Draw text.
        drawLine(matrixStack, text, width - 12 - fontRenderer.width(text), (int) (12f + (line * 12)));
    }

    @SuppressWarnings("unused")
    private void drawBottomString(PoseStack matrixStack, String text, int line) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        // Draw text.
        drawLine(matrixStack, text, (int) (width / 2f - fontRenderer.width(text) / 2f), (int) (height - 29f - (line * 12)));
    }

    @SuppressWarnings("unused")
    private void drawLeftBottomString(PoseStack matrixStack, String text, int line) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int height = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        // Draw text.
        drawLine(matrixStack, text, 12, (int) (height - 29f - (line * 12)));
    }

    @SuppressWarnings("unused")
    private void drawRightBottomString(PoseStack matrixStack, String text, int line) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        // Draw text.
        drawLine(matrixStack, text, width - 12 - fontRenderer.width(text), (int) (height - 29f - (line * 12)));
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    private void drawRightString(PoseStack matrixStack, String text, float mx, float y, int color) {
        // Declare local variables before draw.
        Font fontRenderer = Minecraft.getInstance().font;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        // Draw text.
        drawLine(matrixStack, text, (int) (width - mx - fontRenderer.width(text)), (int) y);
    }

    @Override
    public Formatter<Object> getDefault() {
        return DEFAULT_FORMATTER;
    }

    public IInitPagesEvent createInitEvent() {
        return this::registerPage;
    }
}
