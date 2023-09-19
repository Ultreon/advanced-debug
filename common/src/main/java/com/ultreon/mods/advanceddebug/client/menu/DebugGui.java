package com.ultreon.mods.advanceddebug.client.menu;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.libs.commons.v0.Identifier;
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
import com.ultreon.mods.advanceddebug.mixin.common.ImageButtonAccessor;
import com.ultreon.mods.advanceddebug.registry.ModPreRegistries;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import com.ultreon.mods.advanceddebug.util.*;
import dev.architectury.event.events.client.ClientLifecycleEvent;
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
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.util.FastColor.ARGB32.*;

/**
 * Client listener
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public final class DebugGui extends GuiComponent implements Renderable, IDebugGui {
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

    private DebugGui() {
        ClassUtils.checkCallerClassEquals(DebugGui.class);
        if (INSTANCE != null) {
            throw new Error("Invalid initialization for singleton class " + DebugGui.class.getName());
        }

        ClientLifecycleEvent.CLIENT_STOPPING.register(instance -> requestDisable());

        LifecycleEvent.SERVER_STOPPING.register(instance -> requestDisable());

        LifecycleEvent.SERVER_STOPPED.register(instance -> enable());
    }

    private void enable() {
        RenderSystem.recordRenderCall(() -> {
            enabled = true;
        });
    }

    /**
     * Requests to disable the debug GUI.<br>
     * If it fails it will crash the game.
     */
    public void requestDisable() {
        try {
            if (this.tryRequestDisable()) return;
        } catch (InterruptedException ignored) { }

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
     *         {@code false} - the request was timed-out.
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
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
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

        DebugRenderContext context = new DebugRenderContext(poseStack, rescaledWidth, rescaledHeight) {
            @Override
            protected void drawLine(PoseStack pose, Component text, int x, int y) {
                DebugGui.this.drawLine(pose, text, x, y);
            }
        };

        if (debugPage != null) {
            poseStack.pushPose();
            {
                poseStack.scale((float) ((1 * preferredScale) / scale), (float) ((1 * preferredScale) / scale), 1);
                Identifier resourceLocation = debugPage.getId();
                try {
                    if (Config.SHOW_CURRENT_PAGE.get()) {
                        drawLine(poseStack, Component.literal("Debug Page: " + resourceLocation.toString()), 6, height - 16);
                    }
                    debugPage.render(poseStack, context);
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
            poseStack.popPose();
        } else {
            ModDebugPages.DEFAULT.render(poseStack, context);
        }
        lock.unlock();
    }

    public synchronized static void renderImGui(ImGuiImplGlfw glfw, ImGuiImplGl3 gl3) {
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

    public static void showWindowInfo(com.mojang.blaze3d.platform.Window window) {
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

    @org.jetbrains.annotations.Nullable
    private static ServerLevel getServerLevel(MinecraftServer server) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        return clientLevel == null ? null : server.getLevel(clientLevel.dimension());
    }

    @ApiStatus.Internal
    public static void dispose() {

    }

    private static void showSelectedBlock(SelectedBlock block) {
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
        }, resourceLocation -> {
            level.setBlock(pos, BuiltInRegistries.BLOCK.get(resourceLocation).defaultBlockState(), 0b00000011);
        });

        if (blockEntity != null) {
            if (ImGui.collapsingHeader("Block Entity Info")) {
                ImGui.treePush();
                showBlockEntity(blockEntity);
                ImGui.treePop();
            }
        }
        if (blockState != null) {
            if (ImGui.collapsingHeader("Block State Info")) {
                ImGui.treePush();
                showBlockState(blockState);
                ImGui.treePop();
            }
        }

        if (fluidState != null) {
            if (ImGui.collapsingHeader("Fluid State Info")) {
                ImGui.treePush();
                showBlockState(blockState);
                ImGui.treePop();
            }
        }
    }

    private static void showBlockState(BlockState blockState) {
        ImGuiEx.text("Light Emission:", blockState::getLightEmission);
        ImGuiEx.text("Render Shape:", () -> blockState.getRenderShape().name());
        ImGuiEx.bool("Air:", blockState::isAir);
        ImGuiEx.bool("Signal Source:", blockState::isSignalSource);
        ImGuiEx.bool("Randomly Ticking:", blockState::isRandomlyTicking);

        Material material = blockState.getMaterial();
        if (ImGui.collapsingHeader("Material Info")) {
            ImGui.treePush();
            ImGuiEx.text("Color:", () -> "#%08x".formatted(material.getColor().col));
            ImGuiEx.text("Color ID:", () -> material.getColor().id);
            ImGuiEx.text("Push Reaction:", () -> material.getPushReaction().name());
            ImGuiEx.bool("Flammable:", material::isFlammable);
            ImGuiEx.bool("Liquid:", material::isLiquid);
            ImGuiEx.bool("Replaceable:", material::isReplaceable);
            ImGuiEx.bool("Solid Blocking:", material::isSolidBlocking);
            ImGuiEx.bool("Solid:", material::isSolid);
            ImGui.treePop();
        }
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
                ImGuiEx.text(property.getName() + ":", () -> blockState.getValue(property));
            }
            ImGui.treePop();
        }
    }

    @SuppressWarnings("ConstantValue")
    private static void showBlockEntity(BlockEntity blockEntity) {
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());

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
        ImGuiEx.text("Pos:", () -> entity.blockPosition().toShortString());
        ImGuiEx.text("Rot:", () -> entity.getXRot() + ", " + entity.getYRot());
        ImGuiEx.text("Dim:", () -> entity.level.dimension().location());
        ImGuiEx.text("Name:", entity::getName);
        ImGuiEx.text("UUID:", entity::getStringUUID);
        ImGuiEx.text("Custom Name:", entity::getCustomName);
        ImGuiEx.text("Display Name:", entity::getDisplayName);
        ImGuiEx.text("Scoreboard Name:", entity::getScoreboardName);
        ImGuiEx.bool("Is In Powder Snow:", () -> entity.isInPowderSnow);
        ImGuiEx.bool("No Physics:", () -> entity.noPhysics);
        ImGuiEx.bool("No Culling:", () -> entity.noCulling);
        ImGuiEx.bool("No Gravity:", entity::isNoGravity);
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            ImGuiEx.nbt("NBT:", () -> {
                ServerLevel level = server.getLevel(entity.level.dimension());
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
        if (entity instanceof LivingEntity livingEntity) {
            if (ImGui.collapsingHeader("Living Info")) {
                ImGui.treePush();
                showLivingInfo(livingEntity);
                ImGui.treePop();
            }
        }
        if (entity instanceof Mob mob) {
            if (ImGui.collapsingHeader("Mob Info")) {
                ImGui.treePush();
                showMobInfo(mob);
                ImGui.treePop();
            }
        }
        if (entity instanceof AbstractVillager villager) {
            if (ImGui.collapsingHeader("Base Villager Info")) {
                ImGui.treePush();
                showBaseVillagerInfo(villager);
                ImGui.treePop();
            }
        }
        if (entity instanceof ItemEntity itemEntity) {
            if (ImGui.collapsingHeader("Item Info")) {
                ImGui.treePush();
                showItemEntityInfo(itemEntity);
                ImGui.treePop();
            }
        }
        if (entity instanceof Projectile projectile) {
            if (ImGui.collapsingHeader("Projectile Info")) {
                ImGui.treePush();
                showProjectileInfo(projectile);
                ImGui.treePop();
            }
        }
        if (entity instanceof Player player) {
            if (ImGui.collapsingHeader("Player Info")) {
                ImGui.treePush();
                showPlayerInfo(player);
                ImGui.treePop();
            }
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
        ImGuiEx.text("Age:", itemEntity::getAge);
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
        ImGuiEx.text("Ambient Sound Interval:", mob::getAmbientSoundInterval);
        ImGuiEx.text("Max Head X-Rot:", mob::getMaxHeadXRot);
        ImGuiEx.text("Max Head Y-Rot:", mob::getMaxHeadYRot);
        ImGuiEx.text("Head Rot Speed:", mob::getHeadRotSpeed);
        ImGuiEx.text("Restrict Center:", () -> mob.getRestrictCenter().toShortString());
        ImGuiEx.text("Restrict Radius:", mob::getRestrictRadius);
        ImGuiEx.bool("Is Leashed:", mob::isLeashed);
        ImGuiEx.bool("Is Aggressive:", mob::isAggressive);
        ImGuiEx.bool("Is Left Handed:", mob::isLeftHanded);
        ImGuiEx.bool("Has No AI:", mob::isNoAi);
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
        ImGuiEx.text("Health:", livingEntity::getHealth);
        ImGuiEx.text("Max Health:", livingEntity::getMaxHealth);
        ImGuiEx.text("Used Hand:", () -> livingEntity.getUsedItemHand().name());
        ImGuiEx.text("Death Time:", () -> livingEntity.deathTime);
        ImGuiEx.text("Attack Anim.:", () -> livingEntity.attackAnim);
        ImGuiEx.bool("Attackable:", livingEntity::attackable);

        if (ImGui.collapsingHeader("Combat Tracker")) {
            ImGui.treePush();
            ImGuiEx.text("Combat Duration:", () -> livingEntity.getCombatTracker().getCombatDuration());
            ImGuiEx.text("Cur. Death Message:", () -> livingEntity.getCombatTracker().getDeathMessage());
            ImGuiEx.bool("In Combat:", () -> livingEntity.getCombatTracker().isInCombat());
            ImGuiEx.bool("Taking Damage:", () -> livingEntity.getCombatTracker().isTakingDamage());
            LivingEntity killer = livingEntity.getCombatTracker().getKiller();
            if (ImGui.collapsingHeader("Killer Info") && killer != null) {
                ImGui.treePush();
                showEntity(killer);
                ImGui.treePop();
            }
            CombatEntry entry = livingEntity.getCombatTracker().getLastEntry();
            if (ImGui.collapsingHeader("Last Attack Info") && entry != null) {
                ImGui.treePush();
                ImGuiEx.text("Age (ticks):", entry::getTime);
                ImGuiEx.text("Age (seconds):", () -> entry.getTime() / 20);
                ImGuiEx.text("Age (minutes):", () -> entry.getTime() / 20 / 60);
                ImGuiEx.text("Age (hours):", () -> entry.getTime() / 20 / 60 / 60);
                ImGuiEx.text("Location:", entry::getLocation);
                ImGuiEx.text("Damage:", entry::getDamage);
                ImGuiEx.text("Damage Source:", () -> entry.getSource().type().msgId());
                ImGuiEx.text("Damage Source Pos:", () -> entry.getSource().getSourcePosition());
                ImGuiEx.text("Attacker Name:", entry::getAttackerName);
                ImGuiEx.text("Health Before:", entry::getHealthBeforeDamage);
                ImGuiEx.text("Health After:", entry::getHealthAfterDamage);
                ImGuiEx.text("Fall Distance:", entry::getFallDistance);
                Entity attacker = entry.getAttacker();
                if (ImGui.collapsingHeader("Attacker Info") && attacker != null) {
                    ImGui.treePush();
                    showEntity(attacker);
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
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
}
