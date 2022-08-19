package com.ultreon.mods.advanceddebug.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.common.*;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.client.formatter.FormatterContext;
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList;
import com.ultreon.mods.advanceddebug.client.menu.pages.DefaultPage;
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import com.ultreon.mods.advanceddebug.util.InputUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.util.FastColor.ARGB32.*;

/**
 * Client listener
 */
@SuppressWarnings("unused")
public final class DebugGui implements IIngameOverlay, IDebugGui {
    private static final DebugGui INSTANCE = new DebugGui();
    private static final List<DebugPage> pages = new ArrayList<>();
    private static final DebugPage DEFAULT = new DefaultPage();
    private static final FormatterRegistry FORMATTER_REGISTRY = FormatterRegistry.get();
    private static final Formatter<Object> DEFAULT_FORMATTER = new Formatter<>(Object.class, AdvancedDebug.res("object")) {
        @Override
        public void format(Object obj, IFormatterContext context) {
            Class<?> c = obj.getClass();

            context.classValue(c);
            context.identifier("@");
            context.hexValue(obj.hashCode());
        }
    };
    private static final Map<ResourceLocation, DebugPage> PAGE_REGISTRY = new HashMap<>();
    private static final Marker MARKER = MarkerFactory.getMarker("DebugGui");
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

        double scale = mc.getWindow().getGuiScale();
        double preferredScale = Config.USE_CUSTOM_SCALE.get() ? Config.CUSTOM_SCALE.get() : scale;
        Boolean useCustomScale = Config.USE_CUSTOM_SCALE.get();

        int rescaledWidth = (int) (((double) width * scale) / preferredScale);
        int rescaledHeight = (int) (((double) height * scale) / preferredScale);

        DebugRenderContext context = new DebugRenderContext(pose, rescaledWidth, rescaledHeight) {
            @Override
            protected void drawLine(PoseStack pose, Component text, int x, int y) {
                DebugGui.this.drawLine(pose, text, x, y);
            }
        };

        if (debugPage != null) {
            pose.pushPose();
            {
                pose.scale((float) ((1 * preferredScale) / scale), (float) ((1 * preferredScale) / scale), 1);
                ResourceLocation resourceLocation = debugPage.registryName();
                try {
                    if (Config.SHOW_CURRENT_PAGE.get()) {
                        drawLine(pose, new TextComponent("Debug Page: " + resourceLocation.toString()), 6, height - 16);
                    }
                    debugPage.render(pose, context);
                } catch (Exception e) {
                    if (resourceLocation != null)
                        AdvancedDebug.LOGGER.error(MARKER, "Error rendering debug page {}", resourceLocation, e);
                    else
                        AdvancedDebug.LOGGER.error(MARKER, "Error rendering debug page", e);

                    try {
                        if (resourceLocation != null)
                            context.left(new TextComponent("Error rendering debug page " + resourceLocation).withStyle(RED));
                        else
                            context.left(new TextComponent("Error rendering debug page").withStyle(RED));
                    } catch (Exception e1) {
                        AdvancedDebug.LOGGER.error(MARKER, "Error showing error on debug page", e1);
                    }
                }
            }
            pose.popPose();
        } else {
            DEFAULT.render(pose, context);
        }
    }

    private void drawLine(PoseStack pose, Component text, int x, int y) {
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

    @Deprecated(forRemoval = true)
    private IFormattable getFormatted(String s) {
        return new IFormattable() {
            @SuppressWarnings("removal")
            @Override
            public String toFormattedString() {
                return s;
            }
        };
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

    public Component format(String text, Object obj, Object... objects) {
        StringBuilder sb = new StringBuilder();

        FormatterContext context = new FormatterContext();
        format(obj, context);

        ComponentBuilder builder = new ComponentBuilder();
        builder.append(text, WHITE);
        builder.append(": ", GRAY);
        builder.append(context.build());

        for (Object object : objects) {
            FormatterContext ctx = new FormatterContext();
            format(object, ctx);

            builder.append(", ", GRAY);
            builder.append(ctx.build());
        }

        return builder.build();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void format(Object obj, IFormatterContext context) {
        if (obj == null) {
            context.keyword("null");
        } else if (obj instanceof Class<?> c) {
            context.packageName(((Class<?>) obj).getPackage().getName() + ".");
            context.className(((Class<?>) obj).getSimpleName());
        } else {
            Formatter identified = FORMATTER_REGISTRY.identify(obj.getClass());
            if (identified != null) {
                identified.format(obj, context);
                return;
            }

            DEFAULT_FORMATTER.format(obj, context);
        }
    }

    @Override
    public Formatter<Object> getDefault() {
        return DEFAULT_FORMATTER;
    }

    public IInitPagesEvent createInitEvent() {
        return this::registerPage;
    }
}
