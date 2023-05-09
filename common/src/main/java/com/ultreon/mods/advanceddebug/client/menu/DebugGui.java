package com.ultreon.mods.advanceddebug.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.mods.advanceddebug.AdvancedDebug;
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
import com.ultreon.mods.advanceddebug.mixin.common.KeyMappingAccessor;
import com.ultreon.mods.advanceddebug.registry.ModPreRegistries;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import com.ultreon.mods.advanceddebug.util.ImGuiHandler;
import com.ultreon.mods.advanceddebug.util.InputUtils;
import com.ultreon.mods.advanceddebug.util.SelectedBlocks;
import dev.architectury.platform.Platform;
import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatEntry;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;
import static net.minecraft.util.FastColor.ARGB32.*;

/**
 * Client listener
 */
@SuppressWarnings("unused")
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
    private static final ImGuiFileDialogPaneFun DUMP_NBT_CALLBACK = new ImGuiFileDialogPaneFun() {
        @Override
        public void paneFun(String filter, long userDatas, boolean canContinue) {
            ImGui.text("Filter: " + filter);
        }
    };
    private static final Marker MARKER = MarkerFactory.getMarker("DebugGui");
    public static final ImBoolean SHOW_INFO_WINDOW = new ImBoolean(false);
    public static final ImBoolean SHOW_UTILS = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_PLAYER = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_LEVEL = new ImBoolean(false);
    private static final ImBoolean DI_SHOW_WINDOW = new ImBoolean(false);
    private static boolean imGuiHovered;
    private static boolean imGuiFocused;
    private static boolean renderingImGui = false;
    private Font font;
    private int page = 0;
    private final Minecraft minecraft = Minecraft.getInstance();
    private int width;
    private int height;
    public static final ImBoolean SHOW_IM_GUI = new ImBoolean(false);

    public static Entity selectedEntity;
    public static final SelectedBlocks SELECTED_BLOCKS = new SelectedBlocks();

    private DebugGui() {

    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (Minecraft.getInstance().options.renderDebug) {
            return;
        }

        updateSize();

        font = Minecraft.getInstance().font;

        DebugPage debugPage = getDebugPage();
        Minecraft mc = Minecraft.getInstance();

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
    }

    public synchronized static void renderImGui(ImGuiImplGlfw glfw, ImGuiImplGl3 gl3) {
        if (renderingImGui) return;
        renderingImGui = true;

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
                        ImGui.menuItem("Show Info Window", null, SHOW_INFO_WINDOW);
                        ImGui.endMenu();
                    }
                    ExtensionLoader.invoke(Extension::handleImGuiMenuBar);
                    ImGui.endMenuBar();
                }
                ImGui.end();
            }

            if (SHOW_INFO_WINDOW.get()) showInfoWindow();

            ImGui.render();
            gl3.renderDrawData(ImGui.getDrawData());
        }

        renderingImGui = false;
    }

    private static void showInfoWindow() {
//		Screen currentScreen = getCurrentScreen();
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var window = minecraft.getWindow();
        var screen = minecraft.screen;
        if (ImGui.begin("Debug Info", getDefaultFlags())) {
            if (player != null) {
                if (ImGui.collapsingHeader("Player")) {
                    ImGui.treePush();
                    showLocalPlayer(player);
                    ImGui.treePop();
                }
            }
            if (level != null) {
                if (ImGui.collapsingHeader("Level")) {
                    ImGui.treePush();
                    showLevelInfo(level);
                    ImGui.treePop();
                }
            }
            if (ImGui.collapsingHeader("Window")) {
                ImGui.treePush();
                showWindowInfo(window);
                ImGui.treePop();
            }
            if (ImGui.collapsingHeader("Screen")) {
                ImGui.treePush();
                showScreenInfo(screen);
                ImGui.treePop();
            }
        }
        imGuiHovered = ImGui.isAnyItemHovered() || ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow);
        imGuiFocused = ImGui.isWindowFocused(ImGuiHoveredFlags.AnyWindow);
        ImGui.end();
    }

    private static int getDefaultFlags() {
        boolean mouseGrabbed = Minecraft.getInstance().mouseHandler.isMouseGrabbed();
        var flags = ImGuiWindowFlags.None;
        if (mouseGrabbed) flags |= ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
        return flags;
    }

    private static void showLocalPlayer(LocalPlayer player) {
        imGuiText("Server Brand:", player::getServerBrand);
        imGuiText("Water Vision:", player::getWaterVision);
        showEntity(player);
    }

    private static void showScreenInfo(Screen screen) {
        imGuiText("Classname:", () -> screen == null ? "null" : screen.getClass().getName());
        if (screen != null) {
            imGuiText("Title:", screen::getTitle);
            imGuiText("Is Pause:", screen::isPauseScreen);
            imGuiText("Closeable:", screen::shouldCloseOnEsc);
            imGuiText("Narration:", screen::getNarrationMessage);
            imGuiText("Widget Count:", () -> screen.children().size());

            List<? extends GuiEventListener> children = screen.children();
            showChildren(children);
            if (screen instanceof ImGuiHandler handler && handler.doHandleImGui(Platform.isDevelopmentEnvironment())) {
                handler.handleImGui();
            }
        }
    }

    private static void showWindowInfo(com.mojang.blaze3d.platform.Window window) {
        imGuiText("Size:", () -> window.getWidth() + " × " + window.getHeight());
        imGuiText("Scaled Size:", () -> window.getGuiScaledWidth() + " × " + window.getGuiScaledHeight());
        imGuiText("GUI Scale:", window::getGuiScale);
    }

    private static void showLevelInfo(ClientLevel level) {
        imGuiText("Chunk SS:", level::gatherChunkSourceStats);
        imGuiText("Entity Count:", level::getEntityCount);
        Entity entity = selectedEntity;
        if (entity != null) {
            if (ImGui.collapsingHeader("[" + entity.getId() + "] Entity " + entity.getStringUUID())) {
                ImGui.treePush();
                showEntity(entity);
                ImGui.treePop();
            }
        }
    }

    private static void showEntity(Entity entity) {
        imGuiText("Pos:", () -> entity.blockPosition().toShortString());
        imGuiText("Rot:", () -> entity.getXRot() + ", " + entity.getYRot());
        imGuiText("Dim:", () -> entity.level.dimension().location());
        imGuiText("Name:", entity::getName);
        imGuiText("UUID:", entity::getStringUUID);
        imGuiText("Custom Name:", entity::getCustomName);
        imGuiText("Display Name:", entity::getDisplayName);
        imGuiText("Scoreboard Name:", entity::getScoreboardName);
        imGuiBool("Is In Powder Snow:", () -> entity.isInPowderSnow);
        imGuiBool("No Physics:", () -> entity.noPhysics);
        imGuiBool("No Culling:", () -> entity.noCulling);
        imGuiBool("No Gravity:", entity::isNoGravity);
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (Minecraft.getInstance().isSingleplayer() && server != null) {
            imGuiNbt("NBT:", () -> {
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
                imGuiText("Health:", livingEntity::getHealth);
                imGuiText("Max Health:", livingEntity::getMaxHealth);
                imGuiText("Used Hand:", () -> livingEntity.getUsedItemHand().name());
                imGuiText("Death Time:", () -> livingEntity.deathTime);
                imGuiText("Attack Anim.:", () -> livingEntity.attackAnim);
                imGuiText("Anim. Speed:", () -> livingEntity.animationSpeed);
                imGuiText("Anim. Position:", () -> livingEntity.animationPosition);
                imGuiBool("Attackable:", livingEntity::attackable);

                if (ImGui.collapsingHeader("Combat Tracker")) {
                    ImGui.treePush();
                    imGuiText("Combat Duration:", () -> livingEntity.getCombatTracker().getCombatDuration());
                    imGuiText("Cur. Death Message:", () -> livingEntity.getCombatTracker().getDeathMessage());
                    imGuiBool("In Combat:", () -> livingEntity.getCombatTracker().isInCombat());
                    imGuiBool("Taking Damage:", () -> livingEntity.getCombatTracker().isTakingDamage());
                    LivingEntity killer = livingEntity.getCombatTracker().getKiller();
                    if (ImGui.collapsingHeader("Killer Info") && killer != null) {
                        ImGui.treePush();
                        showEntity(killer);
                        ImGui.treePop();
                    }
                    CombatEntry entry = livingEntity.getCombatTracker().getLastEntry();
                    if (ImGui.collapsingHeader("Last Attack Info") && entry != null) {
                        ImGui.treePush();
                        imGuiText("Age (ticks):", entry::getTime);
                        imGuiText("Age (seconds):", () -> entry.getTime() / 20);
                        imGuiText("Age (minutes):", () -> entry.getTime() / 20 / 60);
                        imGuiText("Age (hours):", () -> entry.getTime() / 20 / 60 / 60);
                        imGuiText("Location:", entry::getLocation);
                        imGuiText("Damage:", entry::getDamage);
                        imGuiText("Damage Source:", () -> entry.getSource().msgId);
                        imGuiText("Attacker Name:", entry::getAttackerName);
                        imGuiText("Health Before:", entry::getHealthBeforeDamage);
                        imGuiText("Health After:", entry::getHealthAfterDamage);
                        imGuiText("Fall Distance:", entry::getFallDistance);
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
                ImGui.treePop();
            }
        }
        if (entity instanceof Mob mob) {
            if (ImGui.collapsingHeader("Living Info")) {
                ImGui.treePush();
                imGuiText("Ambient Sound Interval:", mob::getAmbientSoundInterval);
                imGuiText("Max Head X-Rot:", mob::getMaxHeadXRot);
                imGuiText("Max Head Y-Rot:", mob::getMaxHeadYRot);
                imGuiText("Head Rot Speed:", mob::getHeadRotSpeed);
                imGuiText("Restrict Center:", () -> mob.getRestrictCenter().toShortString());
                imGuiText("Restrict Radius:", mob::getRestrictRadius);
                imGuiBool("Is Leashed:", mob::isLeashed);
                imGuiBool("Is Aggressive:", mob::isAggressive);
                imGuiBool("Is Left Handed:", mob::isLeftHanded);
                imGuiBool("Has No AI:", mob::isNoAi);
                imGuiBool("Restricted:", mob::isWithinRestriction);
                imGuiBool("Persistent:", mob::isPersistenceRequired);

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
                ImGui.treePop();
            }
        }
        if (entity instanceof AbstractVillager villager) {
            if (ImGui.collapsingHeader("Base Villager Info")) {
                ImGui.treePush();
                imGuiText("Villager XP:", villager::getVillagerXp);
                imGuiText("Unhappy Counter:", villager::getUnhappyCounter);

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
                ImGui.treePop();
            }
        }
        if (entity instanceof ItemEntity itemEntity) {
            if (ImGui.collapsingHeader("Item Info")) {
                ImGui.treePush();
                imGuiText("Age:", itemEntity::getAge);
                imGuiText("Owner:", itemEntity::getOwner);
                imGuiText("Thrower:", itemEntity::getThrower);
                if (ImGui.collapsingHeader("Item")) {
                    ImGui.treePush();
                    showItem(itemEntity.getItem());
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
        if (entity instanceof Projectile projectile) {
            if (ImGui.collapsingHeader("Projectile Info")) {
                ImGui.treePush();
                Entity fishing = projectile.getOwner();
                if (ImGui.collapsingHeader("Owner Info")) {
                    ImGui.treePush();
                    if (fishing != null) {
                        showEntity(fishing);
                    }
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
        if (entity instanceof Player player) {
            if (ImGui.collapsingHeader("Player Info")) {
                ImGui.treePush();
                imGuiText("Absorption:", player::getAbsorptionAmount);
                imGuiText("Luck:", player::getLuck);
                imGuiText("Used Inv Slots:", () -> player.getInventory().items.stream().filter(stack -> !stack.isEmpty()).count());
                imGuiBool("Is Hurt:", player::isHurt);
                imGuiBool("Is Affected By Fluids:", player::isAffectedByFluids);
                imGuiBool("Is Creative:", player::isCreative);
                imGuiBool("Is Spectator:", player::isSpectator);
                FishingHook fishing = player.fishing;
                if (ImGui.collapsingHeader("Fishing Hook")) {
                    ImGui.treePush();
                    if (fishing != null) {
                        showEntity(fishing);
                    }
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
    }

    private static void showOffer(MerchantOffer offer) {
        imGuiText("Uses:", () -> "%d / %d".formatted(offer.getUses(), offer.getMaxUses()));
        imGuiText("Demand:", offer::getDemand);
        imGuiText("Price Multiplier:", offer::getPriceMultiplier);
        imGuiText("XP:", offer::getXp);
        imGuiText("Special Price Diff.:", offer::getSpecialPriceDiff);
        imGuiBool("Out of Stock:", offer::isOutOfStock);
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

    private static void showTeam(Team team) {
        imGuiText("Name:", team::getName);
        imGuiText("Color:", () -> "#%06x".formatted(team.getColor().getColor()));
        imGuiText("Collision Rule:", () -> team.getCollisionRule().getDisplayName());
        imGuiText("Death Msg Visibility:", () -> team.getDeathMessageVisibility().getDisplayName());
        imGuiText("Name Tag Visibility:", () -> team.getNameTagVisibility().getDisplayName());
    }

    private static void showItem(ItemStack stack) {
        imGuiText("Count:", stack::getCount);
        imGuiText("Registry Name:", () -> stack.getItem().arch$registryName());
        imGuiText("Bar Color:", () -> "#%08x".formatted(stack.getBarColor()));
        imGuiText("Bar Width:", stack::getBarWidth);
        imGuiText("Damage Value:", stack::getDamageValue);
        imGuiText("Display Name:", stack::getDisplayName);
        imGuiText("Hover Name:", stack::getHoverName);
        imGuiText("Tag:", stack::getTag);
    }

    private static void showChildren(List<? extends GuiEventListener> children) {
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
                        imGuiText("Message:", widget::getMessage);
                        imGuiText("Bounds:", () -> widget.getX() + ", " + widget.getY() + " (" + widget.getWidth() + " × " + widget.getHeight() + ")");
                        imGuiText("Focused:", widget::isFocused);
                        imGuiText("Active:", () -> widget.active);
                        imGuiText("Visible:", () -> widget.visible);
                    }
                    if (child instanceof CycleButton<?> button) {
                        imGuiText("Value:", button::getValue);
                    }
                    if (child instanceof ImageButton button) {
                        imGuiText("Image:", () -> ((ImageButtonAccessor) button).getResourceLocation());
                    }
                    if (child instanceof AbstractButton button) {
                        ImGui.button("Click Button");
                        if (ImGui.isItemClicked()) {
                            button.onPress();
                        }
                    }
                    if (child instanceof EditBox editBox) {
                        imGuiText("Value:", editBox::getValue);
                    }
                    if (child instanceof WorldSelectionList.WorldListEntry entry) {
                        imGuiText("World Name:", entry::getLevelName);
                        ImGui.button("Join World");
                        if (ImGui.isItemClicked()) {
                            entry.joinWorld();
                        }
                    }
                    if (child instanceof WorldSelectionList list) {
                        imGuiText("World Count:", () -> list.children().size());
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

    private static void imGuiBool(String label, BooleanSupplier value) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImGui.checkbox("", value.getAsBoolean());
        } catch (Throwable t) {
            ImGui.text("~@# " + t.getClass().getName() + " #@~");
        }
    }

    private static void imGuiNbt(String label, Supplier<Tag> o) {
        ImGui.text(label);

        ImGui.sameLine();

        ImGui.button("Copy NBT");
        if (ImGui.isItemClicked()) {
            Tag nbt = o.get();
            String visit = new StringTagVisitor().visit(nbt);
            Minecraft.getInstance().keyboardHandler.setClipboard(visit);
        }

        ImGui.sameLine();

        if (ImGui.button("Dump NBT")) {
            ImGuiFileDialog.openModal("browse-key", "Choose File", ".dat", ".", DUMP_NBT_CALLBACK, 250, 1, 42, ImGuiFileDialogFlags.ConfirmOverwrite);
        }

        if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
            if (ImGuiFileDialog.isOk()) {
                Tag nbt = o.get();
                CompoundTag compoundTag;
                if (nbt instanceof CompoundTag) {
                    compoundTag = (CompoundTag) nbt;
                } else {
                    compoundTag = new CompoundTag();
                    compoundTag.put("Data", nbt);
                }

                String filePathName = ImGuiFileDialog.getFilePathName();
                try {
                    NbtIo.writeCompressed(compoundTag, new File(filePathName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ImGuiFileDialog.close();
        }
    }

    private static void imGuiText(String label, Supplier<Object> value) {
        ImGui.text(label);
        ImGui.sameLine();
        Object o;
        try {
            o = value.get();
        } catch (Throwable t) {
            o = "~@# " + t.getClass().getName() + " #@~";
        }
        if (o instanceof Component component) {
            ImGui.text(component.getString());
        } else if (o instanceof Tag nbt) {
            ImGui.button("Copy NBT");
            if (ImGui.isItemClicked()) {
                String visit = new StringTagVisitor().visit(nbt);
                Minecraft.getInstance().keyboardHandler.setClipboard(visit);
            }
            ImGui.sameLine();
            if (nbt instanceof CompoundTag compoundTag) {
                if (ImGui.button("Dump NBT")) {
                    ImGuiFileDialog.openModal("browse-key", "Choose File", ".dat", ".", DUMP_NBT_CALLBACK, 250, 1, 42, ImGuiFileDialogFlags.ConfirmOverwrite);
                }

                if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                    if (ImGuiFileDialog.isOk()) {
                        String filePathName = ImGuiFileDialog.getFilePathName();
                        try {
                            NbtIo.writeCompressed(compoundTag, new File(filePathName));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ImGuiFileDialog.close();
                }
            }
        } else {
            ImGui.text(String.valueOf(o));
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

    public boolean onKeyReleased(int keyCode, int scanCode, int action, int modifiers) {
        if (action == GLFW.GLFW_RELEASE && keyCode == ((KeyMappingAccessor)KeyBindingList.DEBUG_SCREEN).getKey().getValue()) {
            if (InputUtils.isShiftDown()) prev();
            else next();
            return true;
        }
        return false;
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
