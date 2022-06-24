package com.ultreon.mods.advanceddebug.api.init;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.common.Angle;
import com.ultreon.mods.advanceddebug.api.common.IFormattable;
import com.ultreon.mods.advanceddebug.api.common.MoonPhase;
import lombok.experimental.UtilityClass;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.Direction8;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.FastColor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.ultreon.mods.advanceddebug.api.util.MathUtil.roundTo;
import static net.minecraft.ChatFormatting.*;

@UtilityClass
@SuppressWarnings({"rawtypes", "unused"})
public final class ModDebugFormatters {
    private static final IFormatterRegistry REGISTRY = IAdvancedDebug.get().getFormatterRegistry();
    
    public static final Formatter<Number> NUMBER = REGISTRY.register(new Formatter<>(Number.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/number")) {
        @Override
        public void format(Number obj, StringBuilder sb) {
            sb.append(BLUE).append(obj);
        }
    });
    public static final Formatter<Boolean> BOOLEAN = REGISTRY.register(new Formatter<>(Boolean.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/boolean")) {
        @Override
        public void format(Boolean obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append(obj ? "true" : "false");
        }
    });
    public static final Formatter<String> STRING = REGISTRY.register(new Formatter<>(String.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/string")) {
        @Override
        public void format(String obj, StringBuilder sb) {
            sb.append(GREEN);
            sb.append("\"");
            sb.append(obj
                    .replaceAll("\\\\", AQUA + "\\\\" + GREEN)
                    .replaceAll("\n", AQUA + "\\n" + GREEN)
                    .replaceAll("\r", AQUA + "\\r" + GREEN)
                    .replaceAll("\t", AQUA + "\\t" + GREEN)
                    .replaceAll("\b", AQUA + "\\b" + GREEN)
                    .replaceAll("\f", AQUA + "\\f" + GREEN));
            sb.append("\"");
        }
    });
    public static final Formatter<Character> CHARACTER = REGISTRY.register(new Formatter<>(Character.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/character")) {
        @Override
        public void format(Character obj, StringBuilder sb) {
            sb.append(GREEN);
            sb.append("'");
            if (obj.equals('\\')) {
                sb.append(AQUA);
                sb.append("\\\\");
            } else if (obj.equals('\n')) {
                sb.append(AQUA);
                sb.append("\\n");
            } else if (obj.equals('\r')) {
                sb.append(AQUA);
                sb.append("\\r");
            } else if (obj.equals('\t')) {
                sb.append(AQUA);
                sb.append("\\t");
            } else if (obj.equals('\b')) {
                sb.append(AQUA);
                sb.append("\\b");
            } else if (obj.equals('\f')) {
                sb.append(AQUA);
                sb.append("\\f");
            } else {
                sb.append(obj);
            }
            sb.append(GOLD);
            sb.append("'");
        }
    });
    public static final Formatter<List> LIST = REGISTRY.register(new Formatter<>(List.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/list")) {
        @Override
        public void format(List obj, StringBuilder sb) {
            sb.append(GRAY);
            sb.append("[");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                sb.append("]");
                return;
            }

            while (true) {
                Object e = it.next();
                sb.append(e == obj ? (GRAY + "(" + LIGHT_PURPLE + "this List" + GRAY + ")") : format(e));
                if (!it.hasNext()) {
                    sb.append(GRAY).append(']');
                    return;
                }
                sb.append(GRAY).append(',').append(' ');
            }
        }
    });
    public static final Formatter<Map> MAP = REGISTRY.register(new Formatter<>(Map.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/map")) {
        @SuppressWarnings("unchecked")
        @Override
        public void format(Map obj, StringBuilder sb) {
            sb.append(GRAY);
            sb.append("{");

            Iterator<? extends Map.Entry> it = obj.entrySet().iterator();
            if (!it.hasNext()) {
                sb.append("}");
            }

            for (; ; ) {
                Map.Entry<?, ?> e = it.next();
                sb.append(e.getKey() == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Map" + GRAY + ")") : format(e.getKey()));
                sb.append(GRAY).append(": ");
                sb.append(e.getValue() == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Map" + GRAY + ")") : format(e.getValue()));
                if (!it.hasNext()) {
                    sb.append(GRAY).append('}');
                    return;
                }
                sb.append(GRAY).append(", ");
            }
        }
    });
    public static final Formatter<Map.Entry> MAP_ENTRY = REGISTRY.register(new Formatter<>(Map.Entry.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/map/entry")) {
        @Override
        public void format(Map.Entry obj, StringBuilder sb) {
            sb.append(GRAY);

            sb.append(obj.getKey() == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Entry" + GRAY + ")") : format(obj.getKey()));
            sb.append(GRAY).append(": ");
            sb.append(obj.getValue() == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Entry" + GRAY + ")") : format(obj.getValue()));
            sb.append(GRAY).append(", ");
        }
    });
    public static final Formatter<Set> SET = REGISTRY.register(new Formatter<>(Set.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/set")) {
        @Override
        public void format(Set obj, StringBuilder sb) {
            sb.append(GRAY);
            sb.append("{");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                sb.append("}");
            }

            for (; ; ) {
                Object e = it.next();
                sb.append(e == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Set" + GRAY + ")") : format(e));
                if (!it.hasNext()) {
                    sb.append(GRAY).append('}');
                    return;
                }
                sb.append(GRAY).append(',').append(' ');
            }
        }
    });
    public static final Formatter<Collection> COLLECTION = REGISTRY.register(new Formatter<>(Collection.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/set")) {
        @Override
        public void format(Collection obj, StringBuilder sb) {
            sb.append(GRAY);
            sb.append("(");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                sb.append(")");
            }

            for (; ; ) {
                Object e = it.next();
                sb.append(e == obj ? (GRAY + "(" + LIGHT_PURPLE + "this Collection" + GRAY + ")") : format(e));
                if (!it.hasNext()) {
                    sb.append(GRAY).append(')');
                    return;
                }
                sb.append(GRAY).append(',').append(' ');
            }
        }
    });
    public static final Formatter<UUID> UUID = REGISTRY.register(new Formatter<>(UUID.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/uuid")) {
        @Override
        public void format(UUID obj, StringBuilder sb) {
            sb.append(GREEN).append(obj.toString().replaceAll("-", GRAY + "-" + GREEN));
        }
    });
    public static final Formatter<Color> AWT_COLOR = REGISTRY.register(new Formatter<>(Color.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/awt/color")) {
        @Override
        public void format(Color obj, StringBuilder sb) {
            sb.append(GRAY).append("#");
            sb.append(BLUE);
            String s = Integer.toHexString(obj.getRGB());
            sb.append("0".repeat(8 - s.length()));

            sb.append(s);
        }
    });
    public static final Formatter<Entity> ENTITY = REGISTRY.register(new Formatter<>(Entity.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity")) {
        @Override
        public void format(Entity obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("Entity");
            sb.append(WHITE).append(": ");
            sb.append(format(obj.getDisplayName().getString()));
            sb.append(GRAY).append("#");
            sb.append(BLUE).append(obj.getId());
        }
    });
    public static final Formatter<Player> PLAYER = REGISTRY.register(new Formatter<>(Player.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/player")) {
        @Override
        public void format(Player obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("Player");
            sb.append(WHITE).append(": ");
            sb.append(GREEN).append(obj.getGameProfile().getName());
        }
    });
    public static final Formatter<ResourceLocation> RESOURCE_LOCATION = REGISTRY.register(new Formatter<>(ResourceLocation.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/resource_location")) {
        @Override
        public void format(ResourceLocation obj, StringBuilder sb) {
            sb.append(DARK_GREEN).append(obj.getNamespace());
            sb.append(GRAY).append(":");
            sb.append(GREEN).append(obj.getPath());
        }
    });
    public static final Formatter<ItemStack> ITEM_STACK = REGISTRY.register(new Formatter<>(ItemStack.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item_stack")) {
        @Override
        public void format(ItemStack obj, StringBuilder sb) {
            sb.append(format(obj.getItem().getRegistryName())).append(" ");
            sb.append(BLUE).append(obj.getCount()).append("x");
        }
    });
    public static final Formatter<Component> CHAT_COMPONENT = REGISTRY.register(new Formatter<>(Component.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Component obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("Component");
            sb.append(WHITE).append(": ");
            sb.append(format(obj.getString()));
        }
    });
    public static final Formatter<Vec2> VEC2 = REGISTRY.register(new Formatter<>(Vec2.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec2 obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(roundTo(obj.x, 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(roundTo(obj.y, 5)));
        }
    });
    public static final Formatter<Vec3> VEC3 = REGISTRY.register(new Formatter<>(Vec3.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec3 obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(roundTo(obj.x(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(roundTo(obj.y(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("z: ");
            sb.append(format(roundTo(obj.z(), 5)));
        }
    });
    public static final Formatter<Vec3i> VEC3I = REGISTRY.register(new Formatter<>(Vec3i.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec3i obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(obj.getX()));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(obj.getY()));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("z: ");
            sb.append(format(obj.getZ()));
        }
    });
    public static final Formatter<Vector3f> VECTOR3F = REGISTRY.register(new Formatter<>(Vector3f.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector3f obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(roundTo(obj.x(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(roundTo(obj.y(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("z: ");
            sb.append(format(roundTo(obj.z(), 5)));
        }
    });
    public static final Formatter<Vector3d> VECTOR3D = REGISTRY.register(new Formatter<>(Vector3d.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector3d obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(roundTo(obj.x, 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(roundTo(obj.y, 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("z: ");
            sb.append(format(roundTo(obj.z, 5)));
        }
    });
    public static final Formatter<Vector4f> VECTOR4F = REGISTRY.register(new Formatter<>(Vector4f.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector4f obj, StringBuilder sb) {
            sb.append(RED).append("x: ");
            sb.append(format(roundTo(obj.x(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("y: ");
            sb.append(format(roundTo(obj.y(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("z: ");
            sb.append(format(roundTo(obj.z(), 5)));
            sb.append(GRAY).append(", ");
            sb.append(RED).append("w: ");
            sb.append(format(roundTo(obj.w(), 5)));
        }
    });
    public static final Formatter<Pose> POSE = REGISTRY.register(new Formatter<>(Pose.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/pose")) {
        @Override
        public void format(Pose obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("mc-pose");
            sb.append(GRAY).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case STANDING -> "standing";
                case CROUCHING -> "crouching";
                case DYING -> "dying";
                case FALL_FLYING -> "fall_flying";
                case LONG_JUMPING -> "long_jumping";
                case SLEEPING -> "sleeping";
                case SPIN_ATTACK -> "spin_attack";
                case SWIMMING -> "swimming";
            });
        }
    });
    public static final Formatter<MaterialColor> MATERIAL_COLOR = REGISTRY.register(new Formatter<>(MaterialColor.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/material_color")) {
        @Override
        public void format(MaterialColor obj, StringBuilder sb) {
            sb.append(RED).append("id: ");
            sb.append(GOLD).append(obj.id);
            sb.append(RED).append("col: ");
            AWT_COLOR.format(new Color(FastColor.ARGB32.red(obj.col), FastColor.ARGB32.green(obj.col), FastColor.ARGB32.blue(obj.col)), sb);
        }
    });
    public static final Formatter<Block> BLOCK = REGISTRY.register(new Formatter<>(Block.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block")) {
        @Override
        public void format(Block obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("block");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<EntityType> ENTITY_TYPE = REGISTRY.register(new Formatter<>(EntityType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity_type")) {
        @Override
        public void format(EntityType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("entity-type");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<BlockEntityType> BLOCK_ENTITY_TYPE = REGISTRY.register(new Formatter<>(BlockEntityType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block_entity_type")) {
        @Override
        public void format(BlockEntityType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("block-entity-type");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<Advancement> ADVANCEMENT = REGISTRY.register(new Formatter<>(Advancement.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/advancement")) {
        @Override
        public void format(Advancement obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("advancement");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getId(), sb);
        }
    });
    public static final Formatter<Stat> STAT = REGISTRY.register(new Formatter<>(Stat.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(Stat obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("stat");
            sb.append(WHITE).append(": ");
            STRING.format(obj.getName(), sb);
        }
    });
    public static final Formatter<StatType> STAT_TYPE = REGISTRY.register(new Formatter<>(StatType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(StatType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("stat-type");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<VillagerProfession> VILLAGER_PROFESSION = REGISTRY.register(new Formatter<>(VillagerProfession.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(VillagerProfession obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("villager-profession");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<Item> ITEM = REGISTRY.register(new Formatter<>(Item.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item")) {
        @Override
        public void format(Item obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("item");
            sb.append(WHITE).append(": ");
            RESOURCE_LOCATION.format(obj.getRegistryName(), sb);
        }
    });
    public static final Formatter<Rarity> RARITY = REGISTRY.register(new Formatter<>(Rarity.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item/rarity")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Rarity obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("item-rarity");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case COMMON -> "common";
                case UNCOMMON -> "uncommon";
                case RARE -> "rare";
                case EPIC -> "epic";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<RenderShape> RENDER_SHAPE = REGISTRY.register(new Formatter<>(RenderShape.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/render_shape")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(RenderShape obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("render-shape");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case ENTITYBLOCK_ANIMATED -> "entity_block_animated";
                case INVISIBLE -> "invisible";
                case MODEL -> "model";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<BlockBehaviour.OffsetType> BLOCK_OFFSET_TYPE = REGISTRY.register(new Formatter<>(BlockBehaviour.OffsetType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block/offset_type")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(BlockBehaviour.OffsetType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("block-offset-type");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case NONE -> "none";
                case XYZ -> "xyz";
                case XZ -> "model";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Difficulty> DIFFICULTY = REGISTRY.register(new Formatter<>(Difficulty.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/difficulty")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Difficulty obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("difficulty");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case PEACEFUL -> "peaceful";
                case EASY -> "easy";
                case NORMAL -> "normal";
                case HARD -> "hard";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Direction> DIRECTION = REGISTRY.register(new Formatter<>(Direction.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/direction")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Direction obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("direction");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case DOWN -> "down";
                case UP -> "up";
                case NORTH -> "north";
                case SOUTH -> "south";
                case WEST -> "west";
                case EAST -> "east";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Axis> AXIS = REGISTRY.register(new Formatter<>(Axis.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/axis")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Axis obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("axis");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case X -> "x";
                case Y -> "y";
                case Z -> "z";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<AxisDirection> AXIS_DIRECTION = REGISTRY.register(new Formatter<>(AxisDirection.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/axis_direction")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(AxisDirection obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("axis-direction");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case POSITIVE -> "positive";
                case NEGATIVE -> "negative";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Plane> PLANE = REGISTRY.register(new Formatter<>(Plane.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/plane")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Plane obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("plane");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case HORIZONTAL -> "horizontal";
                case VERTICAL -> "vertical";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Direction8> DIRECTION8 = REGISTRY.register(new Formatter<>(Direction8.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/direction_8")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Direction8 obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("direction-8");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case NORTH -> "north";
                case NORTH_EAST -> "north east";
                case EAST -> "east";
                case SOUTH_EAST -> "south east";
                case SOUTH -> "south";
                case SOUTH_WEST -> "south west";
                case WEST -> "west";
                case NORTH_WEST -> "north west";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<FrontAndTop> FRONT_AND_TOP = REGISTRY.register(new Formatter<>(FrontAndTop.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/front_and_top")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(FrontAndTop obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("multi-direction");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case DOWN_EAST -> "down east";
                case DOWN_NORTH -> "down north";
                case DOWN_SOUTH -> "down south";
                case DOWN_WEST -> "down west";
                case UP_EAST -> "up east";
                case UP_NORTH -> "up north";
                case UP_SOUTH -> "up south";
                case UP_WEST -> "up west";
                case WEST_UP -> "west up";
                case EAST_UP -> "east up";
                case NORTH_UP -> "north up";
                case SOUTH_UP -> "south up";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<InteractionHand> INTERACTION_HAND = REGISTRY.register(new Formatter<>(InteractionHand.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/interaction_hand")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(InteractionHand obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("interaction-hand");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case MAIN_HAND -> "main_hand";
                case OFF_HAND -> "off_hand";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<Dist> DIST = REGISTRY.register(new Formatter<>(Dist.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "forge/dist")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(Dist obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("dist");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case CLIENT -> "client";
                case DEDICATED_SERVER -> "dedicated_server";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<MobSpawnType> MOB_SPAWN_TYPE = REGISTRY.register(new Formatter<>(MobSpawnType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity/mob_spawn_type")) {
        @SuppressWarnings("UnnecessaryDefault")
        @Override
        public void format(MobSpawnType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("mob-spawn-type");
            sb.append(WHITE).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case BREEDING -> "breeding";
                case BUCKET -> "bucket";
                case CHUNK_GENERATION -> "chunk_generation";
                case COMMAND -> "command";
                case CONVERSION -> "conversion";
                case DISPENSER -> "dispenser";
                case EVENT -> "event";
                case MOB_SUMMONED -> "summoned";
                case JOCKEY -> "jocky";
                case NATURAL -> "natural";
                case SPAWN_EGG -> "spawn_egg";
                case PATROL -> "patrol";
                case REINFORCEMENT -> "reinforcement";
                case SPAWNER -> "spawner";
                case STRUCTURE -> "structure";
                case TRIGGERED -> "triggered";
                default -> obj.name();
            });
        }
    });
    public static final Formatter<MobType> MOB_TYPE = REGISTRY.register(new Formatter<>(MobType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity/mob_type")) {
        @Override
        public void format(MobType obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("mob-type");
            sb.append(GRAY).append("@");
            sb.append(YELLOW).append(obj.hashCode());
        }
    });
    public static final Formatter<MoonPhase> RT_MOON_PHASE = REGISTRY.register(new Formatter<>(MoonPhase.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "moon_phase")) {
        @Override
        public void format(MoonPhase obj, StringBuilder sb) {
            sb.append(LIGHT_PURPLE).append("moon-phase");
            sb.append(GRAY).append(": ");
            sb.append(GOLD).append(switch (obj) {
                case NEW -> "new";
                case FULL -> "full";
                case FIRST_QUARTER -> "first_quarter";
                case THIRD_QUARTER -> "third_quarter";
                case WANING_CRESCENT -> "waning_crescent";
                case WANING_GIBBOUS -> "waning_gibbous";
                case WAXING_CRESCENT -> "waxing_crescent";
                case WAXING_GIBBOUS -> "waxing_gibbous";
            });
        }
    });
    public static final Formatter<IFormattable> RT_FORMATTABLE = REGISTRY.register(new Formatter<>(IFormattable.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "formattable")) {
        @Override
        public void format(IFormattable obj, StringBuilder sb) {
            sb.append(obj.toFormattedString());
        }
    });
    public static final Formatter<Angle> RT_ANGLE = REGISTRY.register(new Formatter<>(Angle.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "angle")) {
        @Override
        public void format(Angle obj, StringBuilder sb) {
            sb.append(obj.toFormattedString());
        }
    });

    public static void initClass() {

    }
}
