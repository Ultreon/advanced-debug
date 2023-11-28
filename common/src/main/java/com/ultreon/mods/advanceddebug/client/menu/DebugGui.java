package com.ultreon.mods.advanceddebug.client.menu;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.tuple.Pair;
import com.ultreon.libs.commons.v0.util.ClassUtils;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
import com.ultreon.mods.advanceddebug.IllegalThreadError;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui;
import com.ultreon.mods.advanceddebug.api.common.*;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.client.formatter.FormatterContext;
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList;
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry;
import com.ultreon.mods.advanceddebug.extension.ExtensionLoader;
import com.ultreon.mods.advanceddebug.init.ModDebugPages;
import com.ultreon.mods.advanceddebug.inspect.InspectionNode;
import com.ultreon.mods.advanceddebug.inspect.InspectionRoot;
import com.ultreon.mods.advanceddebug.mixin.common.ImageButtonAccessor;
import com.ultreon.mods.advanceddebug.registry.ModPreRegistries;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import com.ultreon.mods.advanceddebug.util.*;
import com.ultreon.mods.lib.util.ServerLifecycle;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import imgui.ImGui;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.util.FastColor.ARGB32.*;

/**
 * Client listener
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public final class DebugGui implements Renderable, IDebugGui {
    private static final DebugGui INSTANCE = new DebugGui();
    private static final List<DebugPage> pages = new ArrayList<>();
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

    private static final Marker MARKER = MarkerFactory.getMarker("DebugGui");

    public static final ImBoolean SHOW_OBJECT_INSPECTION = new ImBoolean(false);
    public static final ImBoolean SHOW_PLAYER_INFO = new ImBoolean(false);
    public static final ImBoolean SHOW_CLIENT_LEVEL_INFO = new ImBoolean(false);
    public static final ImBoolean SHOW_SERVER_LEVEL_INFO = new ImBoolean(false);
    public static final ImBoolean SHOW_WINDOW_INFO = new ImBoolean(false);
    public static final ImBoolean SHOW_SCREEN_INFO = new ImBoolean(false);
    public static final ImBoolean SHOW_UTILS = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_PLAYER = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_LEVEL = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_WINDOW = new ImBoolean(false);
    public static final ImBoolean SHOW_IM_GUI = new ImBoolean(false);

    public static Entity selectedEntity;
    public static Entity selectedServerEntity;
    public static final SelectedBlocks SELECTED_BLOCKS = new SelectedBlocks();

    private static boolean imGuiHovered;
    private static boolean imGuiFocused;
    private static boolean renderingImGui = false;
    private Font font;
    private int page = 0;
    private final Minecraft minecraft = Minecraft.getInstance();
    private int width;
    private int height;
    private final ReentrantLock lock = new ReentrantLock(true);
    private boolean enabled = true;
    private final boolean[] pressed = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private String inspectCurrentPath = "/";
    private String inspectIdxInput = "";

    private DebugGui() {
        ClassUtils.checkCallerClassEquals(DebugGui.class);
        if (INSTANCE != null) {
            throw new Error("Invalid initialization for singleton class " + DebugGui.class.getName());
        }

        ClientLifecycleEvent.CLIENT_STOPPING.register(instance -> requestDisable());
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (player == null) return;
            requestDisable();
        });

        LifecycleEvent.SERVER_STOPPED.register(instance -> enable());
    }

    private void enable() {
        RenderSystem.recordRenderCall(() -> enabled = true);
    }

    /**
     * Requests to disable the debug GUI.<br>
     * If it fails it will crash the game.
     */
    public void requestDisable() {
        try {
            if (this.tryRequestDisable()) return;
        } catch (InterruptedException ignored) {

        }

        CrashReport report = new CrashReport("Time-out!", new TimeoutException("Timed out when waiting on Debug GUI shutdown request"));
        CrashReportCategory shutdownReq = report.addCategory("Debug GUI Shutdown Request");
        shutdownReq.setDetail("Duration", 30000 + "ms");
        shutdownReq.setDetail("Flag set", !enabled);
        Minecraft.crash(report);

        Runtime.getRuntime().halt(0x0000_dead); // Should never run, unless some mod modifies crash handling in a weird way.
    }

    /**
     * Tries to request disabling the debug GUI.<br>
     * If the rendering of the debug GUI frame isn't finished after 30 sec, the request will automatically fail.<br>
     * This method cannot be called from the render thread.
     *
     * @return {@code true} - disabling was done successful<br>
     * {@code false} - the request was timed-out.
     * @throws InterruptedException if the thread was interrupted while waiting on the lock to be available.
     */
    public boolean tryRequestDisable() throws InterruptedException {
        if (RenderSystem.isOnRenderThread()) {
            this.enabled = false;
            return true;
        }

        if (!this.lock.tryLock(30000, TimeUnit.MILLISECONDS)) {
            return false;
        }

        this.enabled = false;

        this.lock.unlock();

        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (!RenderSystem.isOnRenderThread()) return;
        if (!enabled || Minecraft.getInstance().options.renderDebug) return;

        updateSize();

        font = Minecraft.getInstance().font;

        DebugPage debugPage = getDebugPage();
        Minecraft mc = Minecraft.getInstance();

        lock.lock();
        double scale = mc.getWindow().getGuiScale();
        double preferredScale = Config.USE_CUSTOM_SCALE.get() ? Config.CUSTOM_SCALE.get() : scale;
        Boolean useCustomScale = Config.USE_CUSTOM_SCALE.get();

        int rescaledWidth = (int) (((double) width * scale) / preferredScale);
        int rescaledHeight = (int) (((double) height * scale) / preferredScale);

        DebugRenderContext context = new DebugRenderContext(gfx, rescaledWidth, rescaledHeight) {
            @Override
            protected void drawLine(@NotNull GuiGraphics gfx, Component text, int x, int y) {
                DebugGui.this.drawLine(gfx, text, x, y);
            }
        };

        long window = mc.getWindow().getWindow();
        if (SHOW_OBJECT_INSPECTION.get()) {
            this.renderObjectInspection(gfx, window, context, AdvancedDebug.getInstance().inspections);
        }
        if (debugPage != null) {
            gfx.pose().pushPose();
            {
                gfx.pose().scale((float) ((1 * preferredScale) / scale), (float) ((1 * preferredScale) / scale), 1);
                Identifier resourceLocation = debugPage.getId();
                try {
                    if (Config.SHOW_CURRENT_PAGE.get()) {
                        drawLine(gfx, Component.literal("Debug Page: " + resourceLocation.toString()), 6, height - 16);
                    }
                    debugPage.render(gfx, context);
                } catch (Exception e) {
                    if (resourceLocation != null)
                        AdvancedDebug.LOGGER.error(MARKER, "Error rendering debug page {}", resourceLocation, e);
                    else
                        AdvancedDebug.LOGGER.error(MARKER, "Error rendering debug page", e);

                    try {
                        if (resourceLocation != null)
                            context.left(Component.literal("Error rendering debug page " + resourceLocation).withStyle(RED));
                        else
                            context.left(Component.literal("Error rendering debug page").withStyle(RED));
                    } catch (Exception e1) {
                        AdvancedDebug.LOGGER.error(MARKER, "Error showing error on debug page", e1);
                    }
                }
            }
            gfx.pose().popPose();
        } else {
            ModDebugPages.DEFAULT.render(gfx, context);
        }
        lock.unlock();
    }

    private void renderObjectInspection(GuiGraphics gfx, long window, DebugRenderContext ctx, InspectionRoot<Minecraft> inspections) {

        String path = this.inspectCurrentPath;

        Comparator<InspectionNode<?>> comparator = Comparator.comparing(InspectionNode::getName);

        ctx.left(gfx, Component.literal(this.inspectIdxInput == null ? "" : this.inspectIdxInput).withStyle(ChatFormatting.WHITE));

        ctx.left(gfx, Component.literal(path).withStyle(s -> s.withColor(BLUE).withBold(true).withUnderlined(true)));
        ctx.left();

        if (renderInspections(gfx, ctx, inspections, path, comparator)) return;

        if (this.isKeyJustPressed(GLFW.GLFW_KEY_KP_ENTER)) {
            String input = this.inspectIdxInput;
            try {
                int idx = Integer.parseInt(input);

                @Nullable InspectionNode<?> node = inspections.getNode(path);
                if (node == null) {
                    this.inspectCurrentPath = "/";
                    return;
                }
                List<InspectionNode<?>> nodes = node.getNodes().values().stream().sorted(comparator).toList();

                if (nodes.isEmpty()) return;

                if (idx >= 0 && idx < nodes.size()) {
                    path += nodes.get(idx).getName() + "/";
                    this.inspectCurrentPath = path;
                }
                this.inspectIdxInput = "";
            } catch (NumberFormatException ignored) {
                this.inspectIdxInput = "";
            }
        } else if (this.isKeyJustPressed(GLFW.GLFW_KEY_BACKSPACE)) {
            if (this.inspectCurrentPath.equals("/")) {
                return;
            }

            if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
            this.inspectCurrentPath = path.substring(0, path.lastIndexOf("/")) + "/";
        } else if (this.isKeyJustPressed(GLFW.GLFW_KEY_KP_DECIMAL)) {
            this.inspectIdxInput = "";
        } else for (int num = 0; num < 10; num++) {
            int key = GLFW.GLFW_KEY_KP_0 + num;
            if (this.isKeyJustPressed(key)) {
                this.inspectIdxInput += String.valueOf(num);
                break;
            }
        }
    }

    private boolean renderInspections(GuiGraphics gfx, DebugRenderContext ctx, InspectionRoot<Minecraft> inspections, String path, Comparator<InspectionNode<?>> comparator) {
        @Nullable InspectionNode<?> node = inspections.getNode(path);
        if (node == null) {
            this.inspectCurrentPath = "/";
            return true;
        }

        List<InspectionNode<?>> nodes = node.getNodes().values().stream().sorted(comparator).toList();
        int i = 0;
        for (int nodesSize = nodes.size(); i < nodesSize; i++) {
            InspectionNode<?> curNode = nodes.get(i);
            ctx.left(gfx, Component.literal("[" + i + "]: ").withStyle(s -> s.withColor(GOLD).withBold(true)).append(Component.literal(curNode.getName()).withStyle(s -> s.withColor(WHITE).withBold(false))));
        }

        List<Pair<String, String>> elements = node.getElements().entrySet().stream().map(t -> new Pair<>(t.getKey(), t.getValue().get())).sorted(Comparator.comparing(Pair::getFirst)).toList();
        for (Pair<String, String> element : elements) {
            ctx.left(gfx, Component.literal(element.getFirst() + " = ").withStyle(s -> s.withColor(GRAY).withBold(false)).append(Component.literal(element.getSecond()).withStyle(s -> s.withColor(WHITE).withItalic(true))));
        }
        return false;
    }

    private boolean isKeyJustPressed(int key) {
        var pressed = GLFW.glfwGetKey(this.minecraft.getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
        boolean wasPressed = this.pressed[key];
        this.pressed[key] = pressed;
        return pressed && !wasPressed;
    }

    public static synchronized void renderImGui(ImGuiImplGlfw glfw, ImGuiImplGl3 gl3) {
        if (!RenderSystem.isOnRenderThread()) throw new IllegalThreadError();
        if (!get().enabled) return;
        if (renderingImGui) return;
        renderingImGui = true;

        get().lock.lock();
        if (SHOW_IM_GUI.get()) {
            glfw.newFrame();

            int tabBarFlags = ImGuiTabBarFlags.AutoSelectNewTabs | ImGuiTabBarFlags.Reorderable | ImGuiTabBarFlags.FittingPolicyResizeDown | ImGuiTabBarFlags.NoCloseWithMiddleMouseButton;

            MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
            ImGui.newFrame();
            ImGui.setNextWindowPos(0, 0);
            ImGui.setNextWindowSize(Minecraft.getInstance().getWindow().getWidth(), 18);
            ImGui.setNextWindowCollapsed(true);
            if (mouseHandler.isMouseGrabbed()) {
                ImGui.getIO().setMouseDown(new boolean[5]);
                ImGui.getIO().setMousePos(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }

            if (ImGui.begin("MenuBar", ImGuiWindowFlags.NoMove |
                    ImGuiWindowFlags.NoCollapse |
                    ImGuiWindowFlags.AlwaysAutoResize |
                    ImGuiWindowFlags.NoTitleBar |
                    ImGuiWindowFlags.MenuBar |
                    ImGuiWindowFlags.NoFocusOnAppearing |
                    ImGuiWindowFlags.NoBringToFrontOnFocus |
                    ImGuiInputTextFlags.AllowTabInput)) {
                if (ImGui.beginMenuBar()) {
                    if (ImGui.beginMenu("View")) {
                        ImGui.menuItem("Show Player Info", null, SHOW_PLAYER_INFO);
                        ImGui.menuItem("Show Client Level Info", null, SHOW_CLIENT_LEVEL_INFO);
                        ImGui.menuItem("Show Server Level Info", null, SHOW_SERVER_LEVEL_INFO);
                        ImGui.menuItem("Show Window Info", null, SHOW_WINDOW_INFO);
                        ImGui.menuItem("Show Screen Info", null, SHOW_SCREEN_INFO);
                        ImGui.menuItem("Show Object Inspection", null, SHOW_OBJECT_INSPECTION);
                        ImGui.endMenu();
                    }
                    ExtensionLoader.invoke(Extension::handleImGuiMenuBar);
                    ImGui.endMenuBar();
                }
                ImGui.end();
            }

            if (SHOW_PLAYER_INFO.get()) showPlayerInfoWindow();
            if (SHOW_CLIENT_LEVEL_INFO.get()) showClientLevelInfoWindow();
            if (SHOW_SERVER_LEVEL_INFO.get()) showServerLevelInfoWindow();
            if (SHOW_WINDOW_INFO.get()) showWindowInfoWindow();
            if (SHOW_SCREEN_INFO.get()) showScreenInfoWindow();

            imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
            imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);

            ImGui.render();
            gl3.renderDrawData(ImGui.getDrawData());
        }
        get().lock.unlock();

        renderingImGui = false;
    }

    private static void showPlayerInfoWindow() {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var window = minecraft.getWindow();
        var screen = minecraft.screen;
        if (ImGui.begin("Player Info", getDefaultFlags())) {
            if (player != null) {
                showLocalPlayer(player);
            }
        }
        ImGui.end();
    }

    private static void showClientLevelInfoWindow() {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var window = minecraft.getWindow();
        var screen = minecraft.screen;
        if (ImGui.begin("Client Level Info", getDefaultFlags())) {
            if (level != null) {
                showClientLevelInfo(level);
            }
        }
        imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
        imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);
        ImGui.end();
    }

    private static void showServerLevelInfoWindow() {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var server = Minecraft.getInstance().getSingleplayerServer();
        if (ImGui.begin("Server Level Info", getDefaultFlags())) {
            var clientLevel = Minecraft.getInstance().level;
            var serverLevel = server == null || clientLevel == null ? null : server.getLevel(Minecraft.getInstance().level.dimension());
            if (serverLevel != null) {
                showServerLevelInfo(serverLevel);
            }
        }
        imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
        imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);
        ImGui.end();
    }

    private static void showWindowInfoWindow() {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var window = minecraft.getWindow();
        var screen = minecraft.screen;
        if (ImGui.begin("Window Info", getDefaultFlags())) {
            showWindowInfo(window);
        }
        imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
        imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);
        ImGui.end();
    }

    private static void showScreenInfoWindow() {
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var window = minecraft.getWindow();
        var screen = minecraft.screen;
        if (ImGui.begin("Screen Info", getDefaultFlags())) {
            showScreenInfo(screen);
        }
        imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
        imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);
        ImGui.end();
    }

    private static int getDefaultFlags() {
        boolean mouseGrabbed = Minecraft.getInstance().mouseHandler.isMouseGrabbed();
        var flags = ImGuiWindowFlags.None;
        if (mouseGrabbed) flags |= ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoInputs;
        return flags;
    }

    public static void showLocalPlayer(LocalPlayer player) {
        ImGuiEx.text("Server Brand:", player::getServerBrand);
        ImGuiEx.text("Water Vision:", player::getWaterVision);
        showEntity(player);
    }

    public static void showScreenInfo(Screen screen) {
        ImGuiEx.text("Classname:", () -> screen == null ? "null" : screen.getClass().getName());
        if (screen != null) {
            ImGuiEx.text("Title:", screen::getTitle);
            ImGuiEx.text("Is Pause:", screen::isPauseScreen);
            ImGuiEx.text("Closeable:", screen::shouldCloseOnEsc);
            ImGuiEx.text("Narration:", screen::getNarrationMessage);
            ImGuiEx.text("Widget Count:", () -> screen.children().size());

            List<? extends GuiEventListener> children = screen.children();
            showChildren(children);
            if (screen instanceof ImGuiHandler handler && handler.doHandleImGui(Platform.isDevelopmentEnvironment())) {
                handler.handleImGui();
            }
        }
    }

    public static void showWindowInfo(Window window) {
        ImGuiEx.text("Size:", () -> window.getWidth() + " × " + window.getHeight());
        ImGuiEx.text("Scaled Size:", () -> window.getGuiScaledWidth() + " × " + window.getGuiScaledHeight());
        ImGuiEx.text("GUI Scale:", window::getGuiScale);
    }

    public static void showClientLevelInfo(ClientLevel level) {
        Minecraft instance = Minecraft.getInstance();
        float frameTime = instance.getFrameTime();

        ImGuiEx.text("Entity Count:", level::getEntityCount);
        ImGuiEx.editInt("Sky Flash Time:", "ClientLevel[" + level.dimension() + "].SkyFlashTime", level::getSkyFlashTime, level::setSkyFlashTime);
        ImGuiEx.text("Star Brightness:", () -> level.getStarBrightness(frameTime));

        ImGuiEx.editLong("Day Time:", "ClientLevel[" + level.dimension() + "].DayTime", level::getDayTime, level::setDayTime);
        ImGuiEx.editLong("Game Time:", "ClientLevel[" + level.dimension() + "].GameTime", level::getGameTime, level::setGameTime);
        ImGuiEx.text("Time of Day:", () -> level.getTimeOfDay(Minecraft.getInstance().getFrameTime()));

        showLevelInfo(level, frameTime);

        Entity entity = selectedEntity;
        if (entity != null) {
            if (ImGui.collapsingHeader("(Client) [" + entity.getId() + "] Entity " + entity.getStringUUID())) {
                ImGui.treePush();
                showEntity(entity);
                ImGui.treePop();
            }
        }

        SelectedBlock block = SELECTED_BLOCKS.get(level);
        if (block != null) {
            if (ImGui.collapsingHeader("(Client) [" + block.getPos().toShortString() + "] Block " + block.getBlockState().getBlock().arch$registryName())) {
                ImGui.treePush();
                showSelectedBlock(block);
                ImGui.treePop();
            }
        }
    }

    public static void showServerLevelInfo(ServerLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        MinecraftServer server = level.getServer();
        float frameTime = minecraft.getFrameTime();

        ImGuiEx.text("Logical Height:", level::getLogicalHeight);
        ImGuiEx.text("Seed:", level::getSeed);
        if (player != null) {
            BlockPos blockPos = player.blockPosition();
            Vec3 position = player.position();
            ImGuiEx.text("Structure:", () -> {
                Map<Structure, LongSet> allStructuresAt = level.structureManager().getAllStructuresAt(blockPos);
                Optional<ResourceLocation> structure = Lists.newArrayList(allStructuresAt.keySet())
                        .stream()
                        .map(o -> BuiltInRegistries.STRUCTURE_TYPE.getKey(o.type()))
                        .min((o1, o2) -> Objects.compare(o1, o2, (a, b) -> {
                            int i = a.getNamespace().compareTo(b.getNamespace());
                            if (i == 0) {
                                return a.getPath().compareTo(b.getPath());
                            }
                            return i;
                        }));
                return structure.orElse(null);
            });
        }
        ImGuiEx.editBool("No Save:", "ServerLevel[" + level.dimension() + "].NoSave", level.noSave, v -> level.noSave = v);
        ImGuiEx.bool("Flat:", level::isFlat);

        ImGuiEx.editLong("Day Time:", "ServerLevel[" + level.dimension() + "].DayTime", level::getDayTime, level::setDayTime);
        ImGuiEx.text("Game Time:", level::getGameTime);

        showLevelInfo(level, frameTime);

        Entity serverEntity = selectedServerEntity;
        if (serverEntity != null) {
            if (ImGui.collapsingHeader("(Server) [" + serverEntity.getId() + "] Entity " + serverEntity.getStringUUID())) {
                ImGui.treePush();
                showEntity(serverEntity);
                ImGui.treePop();
            }
        }

        SelectedBlock serverBlock = SELECTED_BLOCKS.get(level);
        if (serverBlock != null) {
            if (ImGui.collapsingHeader("(Server) [" + serverBlock.getPos().toShortString() + "] Block " + serverBlock.getBlockState().getBlock().arch$registryName())) {
                ImGui.treePush();
                showSelectedBlock(serverBlock);
                ImGui.treePop();
            }
        }
    }

    public static void showLevelInfo(Level level) {
        showLevelInfo(level, 0.0F);
    }

    public static void showLevelInfo(Level level, float frameTime) {
        ImGuiEx.editFloat("Rain Level", "ServerLevel[" + level.dimension() + "].RainLevel", () -> level.getRainLevel(0F), level::setRainLevel);
        ImGuiEx.editFloat("Thunder Level", "ServerLevel[" + level.dimension() + "].ThunderTime", () -> level.getThunderLevel(0F), level::setRainLevel);
        ImGuiEx.text("Chunk SS:", level::gatherChunkSourceStats);
        ImGuiEx.text("Game Time:", level::getGameTime);
        ImGuiEx.text("Moon Brightness:", level::getMoonBrightness);
        ImGuiEx.text("Moon Phase:", level::getMoonPhase);
        ImGuiEx.text("Time of Day:", () -> level.getTimeOfDay(frameTime));
        ImGuiEx.text("Min. Build Height:", level::getMinBuildHeight);
        ImGuiEx.text("Max. Build Height:", level::getMaxBuildHeight);
        ImGuiEx.text("Sky Darken:", level::getSkyDarken);
        ImGuiEx.text("Sea Level:", level::getSeaLevel);
        ImGuiEx.text("Difficulty:", () -> level.getDifficulty().name());
        ImGuiEx.bool("Debug:", level::isDebug);
        ImGuiEx.bool("Day:", level::isDay);
        ImGuiEx.bool("Night:", level::isNight);
        ImGuiEx.bool("Raining:", level::isRaining);
        ImGuiEx.bool("Thundering:", level::isThundering);
    }

    @Nullable
    public static ServerLevel getServerLevel(MinecraftServer server) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        return clientLevel == null ? null : server.getLevel(clientLevel.dimension());
    }

    public static void showSelectedBlock(SelectedBlock block) {
        Level level = block.getLevel();
        BlockPos pos = block.getPos();
        BlockEntity blockEntity = block.getBlockEntity();
        BlockState blockState = block.getBlockState();
        FluidState fluidState = block.getFluidState();
        Holder<Biome> biome = block.getBiome();

        if (level == null || pos == null) {
            ImGui.text("null");
            return;
        }

        ImGuiEx.text("Position:", pos::toShortString);
        ImGuiEx.text("Level:", level::dimension);
        ImGuiEx.editId("Type:", level.getClass().getSimpleName() + "[" + level.dimension() + "]" + ".BlockStateAt[" + pos.toShortString() + "].Type", () -> {
            try {
                return level.getBlockState(pos).getBlock().arch$registryName();
            } catch (Exception e) {
                return Blocks.VOID_AIR.arch$registryName();
            }
        }, resourceLocation -> level.setBlock(pos, BuiltInRegistries.BLOCK.get(resourceLocation).defaultBlockState(), 0b00000011));

        if (blockEntity != null && ImGui.collapsingHeader("Block Entity Info")) {
            ImGui.treePush();
            showBlockEntity(blockEntity);
            ImGui.treePop();
        }
        if (blockState != null && ImGui.collapsingHeader("Block State Info")) {
            ImGui.treePush();
            showBlockState(level, pos, blockState);
            ImGui.treePop();
        }

        if (fluidState != null && ImGui.collapsingHeader("Fluid State Info")) {
            ImGui.treePush();
            showFluidState(level, pos, fluidState);
            ImGui.treePop();
        }
    }

    public static void showBlockState(Level level, BlockPos pos, BlockState blockState) {
        ImGuiEx.text("Light Emission:", blockState::getLightEmission);
        ImGuiEx.text("Render Shape:", () -> blockState.getRenderShape().name());
        ImGuiEx.bool("Air:", blockState::isAir);
        ImGuiEx.bool("Signal Source:", blockState::isSignalSource);
        ImGuiEx.bool("Randomly Ticking:", blockState::isRandomlyTicking);

        SoundType soundType = blockState.getSoundType();
        if (ImGui.collapsingHeader("Sound Type Info")) {
            ImGui.treePush();
            ImGuiEx.text("Pitch:", () -> soundType.pitch);
            ImGuiEx.text("Volume:", () -> soundType.volume);
            ImGuiEx.text("Place Sound:", () -> soundType.getPlaceSound().getLocation());
            ImGuiEx.text("Break Sound:", () -> soundType.getBreakSound().getLocation());
            ImGuiEx.text("Fall Sound:", () -> soundType.getFallSound().getLocation());
            ImGuiEx.text("Step Sound:", () -> soundType.getStepSound().getLocation());
            ImGuiEx.text("Hit Sound:", () -> soundType.getHitSound().getLocation());
            ImGui.treePop();
        }
        Collection<Property<?>> properties = blockState.getProperties();
        if (ImGui.collapsingHeader("Properties")) {
            ImGui.treePush();
            for (var property : properties) {
                //noinspection rawtypes
                if (property instanceof EnumProperty enumProperty) {
                    //noinspection rawtypes,unchecked
                    ImGuiEx.editEnum(enumProperty.getName() + ":", "", () -> (Enum) blockState.getValue(enumProperty), v -> blockState.setValue(enumProperty, v));
                } else {
                    Comparable<?> value = blockState.getValue(property);
                    ImGuiEx.text(property.getName() + ":", () -> value);
                }
            }
            ImGui.treePop();
        }
    }

    public static void showFluidState(Level level, BlockPos pos, FluidState blockState) {
        ImGuiEx.bool("Randomly Ticking:", blockState::isRandomlyTicking);

        Collection<Property<?>> properties = blockState.getProperties();
        if (ImGui.collapsingHeader("Properties")) {
            ImGui.treePush();
            for (var property : properties) {
                //noinspection rawtypes
                if (property instanceof EnumProperty enumProperty) {
                    //noinspection rawtypes,unchecked
                    ImGuiEx.editEnum(enumProperty.getName() + ":", "", () -> (Enum) blockState.getValue(enumProperty), v -> blockState.setValue(enumProperty, v));
                } else {
                    Comparable<?> value = blockState.getValue(property);
                    ImGuiEx.text(property.getName() + ":", () -> value);
                }
            }
            ImGui.treePop();
        }
    }

    @SuppressWarnings("ConstantValue")
    public static void showBlockEntity(BlockEntity blockEntity) {
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());

        String s = "$(" + blockEntity.getLevel().isClientSide + "):(" + blockEntity.getBlockPos().toShortString() + "):(" + key + ")";

        ImGuiEx.text("Id:", () -> key);
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            ImGuiEx.nbt("NBT:", () -> {
                Level blockEntityLevel = blockEntity.getLevel();
                CompoundTag compoundTag = new CompoundTag();
                if (blockEntityLevel == null) return compoundTag;
                ServerLevel level = server.getLevel(blockEntityLevel.dimension());
                if (level != null) {
                    BlockEntity serverEntity = level.getBlockEntity(blockEntity.getBlockPos());
                    if (serverEntity != null) {
                        compoundTag = serverEntity.saveWithoutMetadata();
                        AdvancedDebug.LOGGER.info("Saved data for %s @ %s".formatted(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(serverEntity.getType()), serverEntity.getBlockPos().toShortString()));
                    }
                }
                return compoundTag == null ? new CompoundTag() : compoundTag;
            });
        }

        ImGuiEx.bool("Is Valid:", () -> blockEntity.getType().isValid(blockEntity.getBlockState()));
        ImGuiEx.bool("Has Level:", blockEntity::hasLevel);
        ImGuiEx.bool("Removed:", blockEntity::isRemoved);
        ImGuiEx.button("Mark dirty:", s + "::markDirty", blockEntity::setChanged);
        ImGuiEx.bool("Only OP can set NBT:", blockEntity::onlyOpCanSetNbt);

        if (blockEntity instanceof Container container && ImGui.collapsingHeader("Container")) {
            ImGui.treePush();
            ImGuiEx.bool("Is Empty:", container::isEmpty);
            ImGuiEx.text("Container Size:", container::getContainerSize);
            ImGuiEx.text("Max Stack Size:", container::getMaxStackSize);
            ImGui.treePop();
        }

        if (ImGui.collapsingHeader("Extensions")) {
            ImGui.treePush();
            ExtensionLoader.invoke(extension -> {
                if (ImGui.collapsingHeader("Extension [%s]".formatted(ExtensionLoader.get(extension)))) {
                    ImGui.treePush();
                    extension.handleBlockEntity(blockEntity);
                    ImGui.treePop();
                }
            });
            ImGui.treePop();
        }
    }

    public static void showEntity(Entity entity) {
//        ImGuiEx.editString("Custom Name:", entity + "::customName", () -> entity.getCustomName().getString(), entity::setCustomName);
        if (ImGui.collapsingHeader("Position")) {
            ImGui.treePush();
            ImGuiEx.editDouble("X:", entity + "::pos::x", entity::getX, v -> entity.setPos(v, entity.getY(), entity.getZ()));
            ImGuiEx.editDouble("Y:", entity + "::pos::y", entity::getY, v -> entity.setPos(entity.getX(), v, entity.getZ()));
            ImGuiEx.editDouble("Z:", entity + "::pos::z", entity::getZ, v -> entity.setPos(entity.getX(), entity.getY(), v));
            ImGui.treePop();
        }

        if (ImGui.collapsingHeader("Rotation")) {
            ImGui.treePush();
            ImGuiEx.editFloat("X:", entity + "::rot::x", entity::getXRot, entity::setXRot);
            ImGuiEx.editFloat("Y:", entity + "::rot::y", entity::getYRot, entity::setYRot);
            ImGui.treePop();
        }

        ImGuiEx.editId("Dim:", entity + "::dimension", () -> entity.level().dimension().location(), resourceLocation -> {
            if (entity.level().isClientSide) return;
            ServerLevel level = ServerLifecycle.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, resourceLocation));
            if (level != null) {
                entity.teleportTo(level, entity.getX(), entity.getY(), entity.getZ(), Set.of(), entity.getXRot(), entity.getYRot());
            }
        });
        ImGuiEx.text("Name:", entity::getName);
        ImGuiEx.text("UUID:", entity::getStringUUID);
        ImGuiEx.text("Display Name:", entity::getDisplayName);
        ImGuiEx.text("Scoreboard Name:", entity::getScoreboardName);
        ImGuiEx.bool("Is In Powder Snow:", () -> entity.isInPowderSnow);
        ImGuiEx.editBool("No Physics:", entity + "::noPhysics", () -> entity.noPhysics, v -> entity.noPhysics = v);
        ImGuiEx.editBool("No Culling:", entity + "::noCulling", () -> entity.noCulling, v -> entity.noCulling = v);
        ImGuiEx.editBool("No Gravity:", entity + "::noGravity", entity::isNoGravity, entity::setNoGravity);
        ImGuiEx.editBool("Blocks Building:", entity + "::blocksBuilding", () -> entity.blocksBuilding, v -> entity.blocksBuilding = v);
        ImGuiEx.editFloat("Fall Distance:", entity + "::fallDistance", () -> entity.fallDistance, v -> entity.fallDistance = v);
        ImGuiEx.editFloat("Fly Distance:", entity + "::flyDistance", () -> entity.flyDist, v -> entity.flyDist = v);
        ImGuiEx.editBool("Fly Distance:", entity + "::flyDistance", () -> entity.hasImpulse, v -> entity.hasImpulse = v);
        ImGuiEx.editBool("Horiz. Collision:", entity + "::horizCollision", () -> entity.horizontalCollision, v -> entity.horizontalCollision = v);
        ImGuiEx.editBool("Hurt Marked:", entity + "::hurtMarked", () -> entity.hurtMarked, v -> entity.hurtMarked = v);
        ImGuiEx.editInt("Invul. Time:", entity + "::invulTime", () -> entity.invulnerableTime, v -> entity.invulnerableTime = v);
        ImGuiEx.editFloat("Move Distance:", entity + "::moveDist", () -> entity.moveDist, v -> entity.moveDist = v);
        ImGuiEx.editBool("Minor Horiz. Collision:", entity + "::minorHorizCollision", () -> entity.minorHorizontalCollision, v -> entity.minorHorizontalCollision = v);
        ImGuiEx.editInt("Tick Count:", entity + "::tickCount", () -> entity.tickCount, v -> entity.tickCount = v);
        ImGuiEx.editBool("Vert. Collision:", entity + "::vertCollision", () -> entity.verticalCollision, v -> entity.verticalCollision = v);
        ImGuiEx.editBool("Vert. Collision Below:", entity + "::vertCollisionBelow", () -> entity.verticalCollisionBelow, v -> entity.verticalCollisionBelow = v);
        ImGuiEx.editFloat("Walk Distance:", entity + "::walkDist", () -> entity.walkDist, v -> entity.walkDist = v);
        ImGuiEx.editInt("Air Supply:", entity + "::airSupply", entity::getAirSupply, entity::setAirSupply);
        ImGuiEx.editFloat("Max Air Supply:", entity + "::maxAirSupply", entity::getMaxAirSupply, v -> {});
        ImGuiEx.editInt("ID:", entity + "::id", entity::getId, entity::setId);
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            ImGuiEx.nbt("NBT:", () -> {
                ServerLevel level = server.getLevel(entity.level().dimension());
                CompoundTag compoundTag = new CompoundTag();
                if (level != null) {
                    Entity serverEntity = level.getEntity(entity.getId());
                    if (serverEntity != null) {
                        serverEntity.saveWithoutId(compoundTag);
                        AdvancedDebug.LOGGER.info("Saved data for %s [%s]".formatted(serverEntity.getType().arch$registryName(), serverEntity.getUUID()));
                    }
                }
                return compoundTag;
            });
        }
        Team team = entity.getTeam();
        if (ImGui.collapsingHeader("Team") && team != null) {
            ImGui.treePush();
            showTeam(team);
            ImGui.treePop();
        }
        if (entity instanceof OwnableEntity ownableEntity) {
            Entity owner = ownableEntity.getOwner();
            if (ImGui.collapsingHeader("Owner") && owner != null) {
                ImGui.treePush();
                showEntity(owner);
                ImGui.treePop();
            }
        }
        if (entity instanceof LivingEntity livingEntity && ImGui.collapsingHeader("Living Info")) {
            ImGui.treePush();
            showLivingInfo(livingEntity);
            ImGui.treePop();
        }
        if (entity instanceof Mob mob && ImGui.collapsingHeader("Mob Info")) {
            ImGui.treePush();
            showMobInfo(mob);
            ImGui.treePop();
        }
        if (entity instanceof AbstractVillager villager && ImGui.collapsingHeader("Base Villager Info")) {
            ImGui.treePush();
            showBaseVillagerInfo(villager);
            ImGui.treePop();
        }
        if (entity instanceof ItemEntity itemEntity && ImGui.collapsingHeader("Item Info")) {
            ImGui.treePush();
            showItemEntityInfo(itemEntity);
            ImGui.treePop();
        }
        if (entity instanceof Projectile projectile && ImGui.collapsingHeader("Projectile Info")) {
            ImGui.treePush();
            showProjectileInfo(projectile);
            ImGui.treePop();
        }
        if (entity instanceof Player player && ImGui.collapsingHeader("Player Info")) {
            ImGui.treePush();
            showPlayerInfo(player);
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Extensions")) {
            ImGui.treePush();
            ExtensionLoader.invoke(extension -> {
                if (ImGui.collapsingHeader("Extension [%s]".formatted(ExtensionLoader.get(extension)))) {
                    ImGui.treePush();
                    extension.handleEntity(entity);
                    ImGui.treePop();
                }
            });
            ImGui.treePop();
        }
    }

    private static void showPlayerInfo(Player player) {
        ImGuiEx.text("Absorption:", player::getAbsorptionAmount);
        ImGuiEx.text("Luck:", player::getLuck);
        ImGuiEx.text("Used Inv Slots:", () -> player.getInventory().items.stream().filter(stack -> !stack.isEmpty()).count());
        ImGuiEx.bool("Is Hurt:", player::isHurt);
        ImGuiEx.bool("Is Affected By Fluids:", player::isAffectedByFluids);
        ImGuiEx.bool("Is Creative:", player::isCreative);
        ImGuiEx.bool("Is Spectator:", player::isSpectator);
        FishingHook fishing = player.fishing;
        if (ImGui.collapsingHeader("Fishing Hook")) {
            ImGui.treePush();
            if (fishing != null) {
                showEntity(fishing);
            }
            ImGui.treePop();
        }
    }

    private static void showProjectileInfo(Projectile projectile) {
        Entity fishing = projectile.getOwner();
        if (ImGui.collapsingHeader("Owner Info")) {
            ImGui.treePush();
            if (fishing != null) {
                showEntity(fishing);
            }
            ImGui.treePop();
        }
    }

    private static void showItemEntityInfo(ItemEntity itemEntity) {
        ImGuiEx.editInt("Age:", itemEntity.toString() + "::age", itemEntity::getAge, v -> {});
        ImGuiEx.text("Owner:", itemEntity::getOwner);
        if (ImGui.collapsingHeader("Item")) {
            ImGui.treePush();
            showItem(itemEntity.getItem());
            ImGui.treePop();
        }
    }

    private static void showBaseVillagerInfo(AbstractVillager villager) {
        ImGuiEx.text("Villager XP:", villager::getVillagerXp);
        ImGuiEx.text("Unhappy Counter:", villager::getUnhappyCounter);

        Player tradingPlayer = villager.getTradingPlayer();
        if (ImGui.collapsingHeader("Trading Player Info") && tradingPlayer != null) {
            ImGui.treePush();
            showItem(villager.getMainHandItem());
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Offers Info")) {
            ImGui.treePush();
            MerchantOffers offers = villager.getOffers();
            for (int i = 0, offersSize = offers.size(); i < offersSize; i++) {
                var offer = offers.get(i);
                if (ImGui.collapsingHeader("[%d]".formatted(i))) {
                    ImGui.treePush();
                    showOffer(offer);
                    ImGui.treePop();
                }
            }
            ImGui.treePop();
        }
    }

    private static void showMobInfo(Mob mob) {
        ImGuiEx.editBool("Aggressive:", mob + "::aggressive", mob::isAggressive, mob::setAggressive);
        ImGuiEx.editBool("Baby:", mob + "::mob::baby_set", mob::isBaby, mob::setBaby);
        ImGuiEx.editBool("Can Pickup Loot:", mob + "::baby", mob::canPickUpLoot, mob::setCanPickUpLoot);
        ImGuiEx.editBool("Left Handed:", mob + "::left_handed", mob::isLeftHanded, mob::setLeftHanded);
        ImGuiEx.editBool("No AI:", mob + "::no_ai", mob::isNoAi, mob::setNoAi);
        ImGuiEx.editFloat("Max Head X-Rot:", mob + "::getMaxHeadXRot", mob::getMaxHeadXRot, v -> {});
        ImGuiEx.editFloat("Max Head Y-Rot:", mob + "::getMaxHeadYRot", mob::getMaxHeadYRot, v -> {});
        ImGuiEx.editFloat("Head Rot Speed:", mob + "::getHeadRotSpeed", mob::getHeadRotSpeed, v -> {});
        ImGuiEx.text("Target:", () -> mob.getTarget().getUUID());
        ImGuiEx.text("Ambient Sound Interval:", mob::getAmbientSoundInterval);
        ImGuiEx.text("Restrict Center:", () -> mob.getRestrictCenter().toShortString());
        ImGuiEx.text("Restrict Radius:", mob::getRestrictRadius);
        ImGuiEx.bool("Is Leashed:", mob::isLeashed);
        ImGuiEx.bool("Restricted:", mob::isWithinRestriction);
        ImGuiEx.bool("Persistent:", mob::isPersistenceRequired);

        LivingEntity target = mob.getTarget();
        if (ImGui.collapsingHeader("Target Info") && target != null) {
            ImGui.treePush();
            showItem(mob.getMainHandItem());
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Off Hand Info")) {
            ImGui.treePush();
            showItem(mob.getOffhandItem());
            ImGui.treePop();
        }
    }

    private static void showLivingInfo(LivingEntity livingEntity) {
        ImGuiEx.editFloat("Health:", livingEntity + "::health", livingEntity::getHealth, livingEntity::setHealth);
        ImGuiEx.editFloat("Max Health:", livingEntity + "::maxHealth", livingEntity::getMaxHealth, v -> {});
        ImGuiEx.editInt("Death Time:", livingEntity + "::deathTime", () -> livingEntity.deathTime, v -> livingEntity.deathTime = v);
        ImGuiEx.editFloat("Attack Anim:", livingEntity + "::attackAnim", () -> livingEntity.attackAnim, v -> livingEntity.attackAnim = v);
        ImGuiEx.editInt("Swing Time:", livingEntity + "::swingTime", () -> livingEntity.swingTime, v -> livingEntity.swingTime = v);
        ImGuiEx.editBool("Swinging:", livingEntity + "::swinging", () -> livingEntity.swinging, v -> livingEntity.swinging = v);

        ImGuiEx.editFloat("Absorption Amount:", livingEntity + "::absorptionAmount", livingEntity::getAbsorptionAmount, livingEntity::setAbsorptionAmount);
        ImGuiEx.editInt("Armor Value:", livingEntity + "::armorValue", livingEntity::getArmorValue, v -> {});
        ImGuiEx.editInt("Arrow Count:", livingEntity + "::arrowCount", livingEntity::getArrowCount, livingEntity::setArrowCount);
        ImGuiEx.editInt("Experience Reward:", livingEntity + "::experienceReward", livingEntity::getExperienceReward, v -> {});
        ImGuiEx.editInt("Fall Flying Ticks:", livingEntity + "::fallFlyingTicks", livingEntity::getFallFlyingTicks, v -> {});
        ImGuiEx.editBool("Fire Immune:", livingEntity + "::fireImmune", livingEntity::fireImmune, v -> {});
        ImGuiEx.editInt("Hurt Time:", livingEntity + "::hurtTime", () -> livingEntity.hurtTime, v -> livingEntity.hurtTime = v);
        ImGuiEx.editInt("Hurt Duration:", livingEntity + "::hurtDuration", () -> livingEntity.hurtDuration, v -> livingEntity.hurtDuration = v);
        ImGuiEx.editFloat("Hurt Direction:", livingEntity + "::hurtDirection", livingEntity::getHurtDir, v -> {});
        ImGuiEx.editFloat("Invulnerable Duration:", livingEntity + "::invulnerableDuration", () -> livingEntity.invulnerableDuration, v -> {});
        ImGuiEx.editFloat("Jump Boost Power:", livingEntity + "::jumpBoostPower", livingEntity::getJumpBoostPower, v -> {});
        ImGuiEx.editFloat("Old Attack Anim:", livingEntity + "::oldAttackAnim", () -> livingEntity.oAttackAnim, v -> livingEntity.oAttackAnim = v);
        ImGuiEx.editBool("On Climable:", livingEntity + "::onClimable", livingEntity::onClimbable, v -> {});
        ImGuiEx.editInt("Remove Arrow Time:", livingEntity + "::removeArrowTime", () -> livingEntity.removeArrowTime, v -> livingEntity.removeArrowTime = v);
        ImGuiEx.editInt("Remove Stinger Time:", livingEntity + "::removeStingerTime", () -> livingEntity.removeStingerTime, v -> livingEntity.removeStingerTime = v);
        ImGuiEx.button("Remove All Effects:", livingEntity + "::removeAllEffects()", livingEntity::removeAllEffects);
        ImGuiEx.editFloat("Rotation A:", livingEntity + "::rotA", () -> livingEntity.rotA, v -> {});
        ImGuiEx.editFloat("Speed:", livingEntity + "::speed", livingEntity::getSpeed, livingEntity::setSpeed);
        ImGuiEx.editEnum("Swinging Arm:", livingEntity + "::swingingArm", () -> livingEntity.swingingArm, v -> livingEntity.swingingArm = v);
        ImGuiEx.editFloat("Time Offsets:", livingEntity + "::timeOffs", () -> livingEntity.timeOffs, v -> {});
        ImGuiEx.editFloat("Y Body Rotation:", livingEntity + "::yBodyRot", () -> livingEntity.yBodyRot, v -> livingEntity.yBodyRot = v);
        ImGuiEx.editFloat("Y Head Rotation:", livingEntity + "::yHeadRot", () -> livingEntity.yBodyRot, v -> livingEntity.yBodyRot = v);
        ImGuiEx.editFloat("Old Y Body Rotation:", livingEntity + "::yBodyRot0", () -> livingEntity.yBodyRotO, v -> livingEntity.yBodyRotO = v);
        ImGuiEx.editFloat("Old Y Head Rotation:", livingEntity + "::yHeadRot0", () -> livingEntity.yHeadRotO, v -> livingEntity.yHeadRotO = v);
        ImGuiEx.editFloat("X acceleration:", livingEntity + "::xxa", () -> livingEntity.xxa, v -> livingEntity.xxa = v);
        ImGuiEx.editFloat("Y acceleration:", livingEntity + "::yya", () -> livingEntity.yya, v -> livingEntity.yya = v);
        ImGuiEx.editFloat("Z acceleration:", livingEntity + "::zza", () -> livingEntity.zza, v -> livingEntity.zza = v);

        ImGuiEx.text("Distance:", () -> livingEntity.distanceTo(Minecraft.getInstance().getCameraEntity()));
        ImGuiEx.text("Used Hand:", () -> livingEntity.getUsedItemHand().name());
        ImGuiEx.text("Death Time:", () -> livingEntity.deathTime);
        ImGuiEx.text("Attack Anim.:", () -> livingEntity.attackAnim);
        ImGuiEx.bool("Attackable:", livingEntity::attackable);

        if (ImGui.collapsingHeader("Flags (is)")) {
            ImGui.treePush();
            ImGuiEx.editBool("Is Alive:", livingEntity + "::isAlive", livingEntity::isAlive, v -> {});
            ImGuiEx.editBool("Is Baby:", livingEntity + "::isBaby", livingEntity::isBaby, v -> {});
            ImGuiEx.editBool("Is Blocking:", livingEntity + "::isBlocking", livingEntity::isBlocking, v -> {});
            ImGuiEx.editBool("Is Auto Spin Attack:", livingEntity + "::isAutoSpinAttack", livingEntity::isAutoSpinAttack, v -> {});
            ImGuiEx.editBool("Is Affected By Potions:", livingEntity + "::isAffectedByPotions", livingEntity::isAffectedByPotions, v -> {});
            ImGuiEx.editBool("Is Glowing:", livingEntity + "::isGlowing", livingEntity::isCurrentlyGlowing, livingEntity::setGlowingTag);
            ImGuiEx.editBool("Is Dead or Dying:", livingEntity + "::isDeadOrDying", livingEntity::isDeadOrDying, v -> {});
            ImGuiEx.editBool("Is Fall Flying:", livingEntity + "::isFallFlying", livingEntity::isFallFlying, v -> {});
            ImGuiEx.editBool("Is In Wall:", livingEntity + "::isInWall", livingEntity::isInWall, v -> {});
            ImGuiEx.editBool("Is Pickable:", livingEntity + "::isPickable", livingEntity::isPickable, v -> {});
            ImGuiEx.editBool("Is Pushable:", livingEntity + "::isPushable", livingEntity::isPushable, v -> {});
            ImGuiEx.editBool("Is Sensitive To Water:", livingEntity + "::isSensitiveToWater", livingEntity::isSensitiveToWater, v -> {});
            ImGuiEx.editBool("Is Sleeping:", livingEntity + "::isSleeping", livingEntity::isSleeping, v -> {});
            ImGuiEx.editBool("Is Using an Item:", livingEntity + "::isUsingItem", livingEntity::isUsingItem, v -> {});
            ImGuiEx.editBool("Is Visually Swimming:", livingEntity + "::isVisuallySwimming", livingEntity::isVisuallySwimming, v -> {});
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Flags (can)")) {
            ImGui.treePush();
            ImGuiEx.editBool("Can Be Seen As Enemy:", livingEntity + "::canBeSeenAsEnemy", livingEntity::canBeSeenAsEnemy, v -> {});
            ImGuiEx.editBool("Can Be Seen By Anyone:", livingEntity + "::canBeSeenByAnyone", livingEntity::canBeSeenByAnyone, v -> {});
            ImGuiEx.editBool("Can Freeze:", livingEntity + "::canFreeze", livingEntity::canFreeze, v -> {});
            ImGuiEx.editBool("Can Breathe Underwater:", livingEntity + "::canBreatheUnderwater", livingEntity::canBreatheUnderwater, v -> {});
            ImGuiEx.editBool("Can Change Dimension:", livingEntity + "::canChangeDimension", livingEntity::canChangeDimensions, v -> {});
            ImGuiEx.editBool("Can Disable Shield:", livingEntity + "::canDisableShield", livingEntity::canDisableShield, v -> {});
            ImGuiEx.editBool("Can Spawn Soulspeed Particle:", livingEntity + "::canSpawnSoulSpeedParticle", livingEntity::canSpawnSoulSpeedParticle, v -> {});
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Combat Tracker")) {
            ImGui.treePush();
            ImGuiEx.text("Combat Duration:", () -> livingEntity.getCombatTracker().getCombatDuration());
            ImGuiEx.text("Cur. Death Message:", () -> livingEntity.getCombatTracker().getDeathMessage());
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Main Hand Info")) {
            ImGui.treePush();
            showItem(livingEntity.getMainHandItem());
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Off Hand Info")) {
            ImGui.treePush();
            showItem(livingEntity.getOffhandItem());
            ImGui.treePop();
        }
        if (ImGui.collapsingHeader("Active Effects")) {
            ImGui.treePush();
            List<MobEffectInstance> activeEffects = Lists.newArrayList(livingEntity.getActiveEffects());
            for (int i = 0, activeEffectsSize = activeEffects.size(); i < activeEffectsSize; i++) {
                MobEffectInstance activeEffect = activeEffects.get(i);
                if (ImGui.collapsingHeader("[%d] Effect Instance".formatted(i))) {
                    ImGui.treePush();
                    showEffectInstance(activeEffect);
                    ImGui.treePop();
                }
            }
            ImGui.treePop();
        }
    }

    public static void showEffectInstance(MobEffectInstance instance) {
        ImGuiEx.text("Id:", () -> BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect()));
        ImGuiEx.text("Duration:", () -> MobEffectUtil.formatDuration(instance, 1.0f));
        ImGuiEx.text("Duration:", () -> instance.getFactorData().orElse(null));
    }

    public static void showOffer(MerchantOffer offer) {
        ImGuiEx.text("Uses:", () -> "%d / %d".formatted(offer.getUses(), offer.getMaxUses()));
        ImGuiEx.text("Demand:", offer::getDemand);
        ImGuiEx.text("Price Multiplier:", offer::getPriceMultiplier);
        ImGuiEx.text("XP:", offer::getXp);
        ImGuiEx.text("Special Price Diff.:", offer::getSpecialPriceDiff);
        ImGuiEx.bool("Out of Stock:", offer::isOutOfStock);
        ItemStack baseCostA = offer.getBaseCostA();
        if (ImGui.collapsingHeader("Base Cost A")) {
            ImGui.treePush();
            showItem(baseCostA);
            ImGui.treePop();
        }
        ItemStack costA = offer.getCostA();
        if (ImGui.collapsingHeader("Cost A")) {
            ImGui.treePush();
            showItem(costA);
            ImGui.treePop();
        }
        ItemStack costB = offer.getCostB();
        if (ImGui.collapsingHeader("Cost B")) {
            ImGui.treePush();
            showItem(costB);
            ImGui.treePop();
        }
        ItemStack result = offer.getResult();
        if (ImGui.collapsingHeader("Result")) {
            ImGui.treePush();
            showItem(result);
            ImGui.treePop();
        }
    }

    public static void showTeam(Team team) {
        ImGuiEx.text("Name:", team::getName);
        ImGuiEx.text("Color:", () -> "#%06x".formatted(team.getColor().getColor()));
        ImGuiEx.text("Collision Rule:", () -> team.getCollisionRule().getDisplayName());
        ImGuiEx.text("Death Msg Visibility:", () -> team.getDeathMessageVisibility().getDisplayName());
        ImGuiEx.text("Name Tag Visibility:", () -> team.getNameTagVisibility().getDisplayName());
    }

    public static void showItem(ItemStack stack) {
        ImGuiEx.text("Count:", stack::getCount);
        ImGuiEx.text("Registry Name:", () -> stack.getItem().arch$registryName());
        ImGuiEx.text("Bar Color:", () -> "#%08x".formatted(stack.getBarColor()));
        ImGuiEx.text("Bar Width:", stack::getBarWidth);
        ImGuiEx.text("Damage Value:", stack::getDamageValue);
        ImGuiEx.text("Display Name:", stack::getDisplayName);
        ImGuiEx.text("Hover Name:", stack::getHoverName);
        ImGuiEx.text("Tag:", stack::getTag);
    }

    public static void showChildren(List<? extends GuiEventListener> children) {
        if (children != null && ImGui.collapsingHeader("Widgets")) {
            ImGui.treePush();
            for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
                GuiEventListener child = children.get(i);
                if (child == null) continue;

                if (ImGui.collapsingHeader("[" + i + "] " + child.getClass().getName())) {
                    ImGui.treePush();
                    if (child instanceof ImGuiHandler handler && handler.doHandleImGui(Platform.isDevelopmentEnvironment())) {
                        handler.handleImGui();
                    }
                    if (child instanceof AbstractWidget widget) {
                        ImGuiEx.text("Message:", widget::getMessage);
                        ImGuiEx.text("Bounds:", () -> widget.getX() + ", " + widget.getY() + " (" + widget.getWidth() + " × " + widget.getHeight() + ")");
                        ImGuiEx.text("Focused:", widget::isFocused);
                        ImGuiEx.text("Active:", () -> widget.active);
                        ImGuiEx.text("Visible:", () -> widget.visible);
                    }
                    if (child instanceof CycleButton<?> button) {
                        ImGuiEx.text("Value:", button::getValue);
                    }
                    if (child instanceof ImageButton button) {
                        ImGuiEx.text("Image:", () -> ((ImageButtonAccessor) button).getResourceLocation());
                    }
                    if (child instanceof AbstractButton button) {
                        ImGui.button("Click Button");
                        if (ImGui.isItemClicked()) {
                            button.onPress();
                        }
                    }
                    if (child instanceof EditBox editBox) {
                        ImGuiEx.text("Value:", editBox::getValue);
                    }
                    if (child instanceof WorldSelectionList.WorldListEntry entry) {
                        ImGuiEx.text("World Name:", entry::getLevelName);
                        ImGui.button("Join World");
                        if (ImGui.isItemClicked()) {
                            entry.joinWorld();
                        }
                    }
                    if (child instanceof WorldSelectionList list) {
                        ImGuiEx.text("World Count:", () -> list.children().size());
                    }
                    if (child instanceof ContainerEventHandler container) {
                        showChildren(container.children());
                    }
                    ImGui.treePop();
                }
            }
            ImGui.treePop();
        }
    }

    private static void showUtils() {

    }

    private void updateSize() {
        width = minecraft.getWindow().getGuiScaledWidth();
        height = minecraft.getWindow().getGuiScaledHeight();
    }

    private void drawLine(@NotNull GuiGraphics gfx, Component text, int x, int y) {
        gfx.fill(x, y, x + font.width(text) + 2, (y - 1) + font.lineHeight + 2, 0x5f000000);
        gfx.drawString(this.font, text, x + 1, y + 1, 0xffffff, false);
    }

    public static DebugGui get() {
        return INSTANCE;
    }

    public List<DebugPage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends DebugPage> T registerPage(Identifier id, T page) {
        pages.add(page);

        ModPreRegistries.DEBUG_PAGE.register(id, page);

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

    public void tick() {
        if (KeyBindingList.DEBUG_SCREEN.consumeClick()) {
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

    public static boolean isImGuiHovered() {
        return imGuiHovered;
    }

    public static boolean isImGuiFocused() {
        return imGuiFocused;
    }

    @ApiStatus.Internal
    public static void dispose() {

    }
}
