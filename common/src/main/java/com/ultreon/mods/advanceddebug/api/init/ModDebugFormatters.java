package com.ultreon.mods.advanceddebug.api.init;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter;
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.common.Angle;
import com.ultreon.mods.advanceddebug.api.common.IFormattable;
import com.ultreon.mods.advanceddebug.api.common.MoonPhase;
import dev.architectury.utils.Env;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.Direction8;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.FastColor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.ultreon.mods.advanceddebug.api.util.MathUtil.roundTo;

@SuppressWarnings({"rawtypes", "unused"})
public final class ModDebugFormatters {
    private static final IFormatterRegistry REGISTRY = IAdvancedDebug.get().getFormatterRegistry();

    public static final Formatter<Number> NUMBER = REGISTRY.register(new Formatter<>(Number.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/number")) {
        @Override
        public void format(Number obj, IFormatterContext context) {
            context.number(obj);
        }
    });
    public static final Formatter<Boolean> BOOLEAN = REGISTRY.register(new Formatter<>(Boolean.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/boolean")) {
        @Override
        public void format(Boolean obj, IFormatterContext context) {
            context.keyword(obj ? "true" : "false");
        }
    });
    public static final Formatter<String> STRING = REGISTRY.register(new Formatter<>(String.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/string")) {
        @Override
        public void format(String obj, IFormatterContext context) {
            context.string("\"").stringEscaped(obj).string("\"");
        }
    });
    public static final Formatter<Character> CHARACTER = REGISTRY.register(new Formatter<>(Character.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/character")) {
        @Override
        public void format(Character obj, IFormatterContext context) {
            context.string("'").charsEscaped(obj.toString()).string("'");
        }
    });
    public static final Formatter<Enum> ENUM = REGISTRY.register(new Formatter<>(Enum.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/enum")) {
        @Override
        public void format(Enum obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<List> LIST = REGISTRY.register(new Formatter<>(List.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/list")) {
        @Override
        public void format(List obj, IFormatterContext context) {
            context.operator("[");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator("]");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("[...]");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator("]");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Map> MAP = REGISTRY.register(new Formatter<>(Map.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/map")) {
        @SuppressWarnings("unchecked")
        @Override
        public void format(Map obj, IFormatterContext context) {
            context.operator("{");

            Iterator<? extends Map.Entry> it = obj.entrySet().iterator();
            if (!it.hasNext()) {
                context.operator("}");
                return;
            }

            while (true) {
                Map.Entry<?, ?> e = it.next();
                if (e.getKey() == obj) {
                    context.keyword("{...}");
                } else if (e.getKey() == e) {
                    context.keyword("<...>");
                } else {
                    context.other(e.getKey());
                }

                context.operator(":");

                if (e.getValue() == obj) {
                    context.keyword("{...}");
                } else if (e.getValue() == e) {
                    context.keyword("<...>");
                } else {
                    context.other(e.getValue());
                }

                if (!it.hasNext()) {
                    context.operator("}");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Map.Entry> MAP_ENTRY = REGISTRY.register(new Formatter<>(Map.Entry.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/map/entry")) {
        @Override
        public void format(Map.Entry obj, IFormatterContext context) {
            if (obj.getKey() == obj) {
                context.keyword("<...>");
            } else {
                context.other(obj.getKey());
            }
            context.operator(": ");
            if (obj.getValue() == obj) {
                context.keyword("<...>");
            } else {
                context.other(obj.getValue());
            }
        }
    });
    public static final Formatter<Set> SET = REGISTRY.register(new Formatter<>(Set.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/set")) {
        @Override
        public void format(Set obj, IFormatterContext context) {
            context.operator("{");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator("}");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("{...}");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator("}");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Collection> COLLECTION = REGISTRY.register(new Formatter<>(Collection.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/set")) {
        @Override
        public void format(Collection obj, IFormatterContext context) {
            context.operator("(");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator(")");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("(...)");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator(")");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<UUID> UUID = REGISTRY.register(new Formatter<>(UUID.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/uuid")) {
        @Override
        public void format(UUID obj, IFormatterContext context) {
            Iterator<String> iterator = Arrays.stream(obj.toString().split("-")).iterator();
            if (!iterator.hasNext()) return;

            while (true) {
                String s = iterator.next();
                context.hex(s);
                if (!iterator.hasNext()) return;
                context.operator("-");
            }
        }
    });
    public static final Formatter<Color> AWT_COLOR = REGISTRY.register(new Formatter<>(Color.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "java/awt/color")) {
        @Override
        public void format(Color obj, IFormatterContext context) {
            String s = Integer.toHexString(obj.getRGB());

            context.operator("#");
            context.hex("0".repeat(8 - s.length()) + s);
        }
    });
    public static final Formatter<Entity> ENTITY = REGISTRY.register(new Formatter<>(Entity.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity")) {
        @Override
        public void format(Entity obj, IFormatterContext context) {
            context.className(obj.getClass().getName()).space().other(obj.getUUID());
            context.operator(" (").other(obj.getDisplayName()).operator(") #").other(obj.getId());
        }
    });
    public static final Formatter<Player> PLAYER = REGISTRY.register(new Formatter<>(Player.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/player")) {
        @Override
        public void format(Player obj, IFormatterContext context) {
            context.className("Player").space().other(obj.getUUID());
            context.operator(" (").other(obj.getGameProfile().getName()).operator(")");
        }
    });
    public static final Formatter<ResourceLocation> RESOURCE_LOCATION = REGISTRY.register(new Formatter<>(ResourceLocation.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/resource_location")) {
        @Override
        public void format(ResourceLocation obj, IFormatterContext context) {
            if (IAdvancedDebug.get().isNamespaceSpaced()) {
                context.operator("(")
                        .className(obj.getNamespace().replaceAll("_", " "))
                        .operator(") ")
                        .identifier(obj.getPath().replaceAll("_", " ").replaceAll("/", " -> "));
            } else if (IAdvancedDebug.get().isBubbleBlasterIdEnabled()) {
                String namespace = obj.getNamespace();
                if (namespace.equals(ResourceLocation.DEFAULT_NAMESPACE)) namespace = "bubbles";
                context.identifier(obj.getPath())
                        .operator("@")
                        .className(namespace.replaceAll("_", "."));
            } else {
                context.className(obj.getNamespace())
                        .operator(":")
                        .identifier(obj.getPath());
            }
        }
    });
    public static final Formatter<ItemStack> ITEM_STACK = REGISTRY.register(new Formatter<>(ItemStack.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item_stack")) {
        @Override
        public void format(ItemStack obj, IFormatterContext context) {
            context.intValue(obj.getCount())
                    .operator("× ");
            context.other(obj.getItem().arch$registryName());
        }
    });
    public static final Formatter<Component> CHAT_COMPONENT = REGISTRY.register(new Formatter<>(Component.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Component obj, IFormatterContext context) {
            context.className(obj.getClass().getSimpleName())
                    .space()
                    .string("\"")
                    .stringEscaped(obj.getString())
                    .string("\"");
        }
    });

    public static final Formatter<MutableComponent> MUTABLE_COMPONENT = REGISTRY.register(new Formatter<>(MutableComponent.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(MutableComponent obj, IFormatterContext context) {
            context.className("mutable component")
                    .space()
                    .other(obj.getString());
        }
    });

    public static final Formatter<Vec2> VEC2 = REGISTRY.register(new Formatter<>(Vec2.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec2 obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    public static final Formatter<Vec3> VEC3 = REGISTRY.register(new Formatter<>(Vec3.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec3 obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
            context.separator();
            context.parameter("z", roundTo(obj.z, 5));
        }
    });
    public static final Formatter<Vec3i> VEC3I = REGISTRY.register(new Formatter<>(Vec3i.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vec3i obj, IFormatterContext context) {
            context.parameter("x", obj.getX());
            context.separator();
            context.parameter("y", obj.getY());
            context.separator();
            context.parameter("z", obj.getZ());
        }
    });
    public static final Formatter<Vector3f> VECTOR3F = REGISTRY.register(new Formatter<>(Vector3f.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector3f obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x(), 5));
            context.separator();
            context.parameter("y", roundTo(obj.y(), 5));
            context.separator();
            context.parameter("z", roundTo(obj.z(), 5));
        }
    });
    public static final Formatter<Vector3d> VECTOR3D = REGISTRY.register(new Formatter<>(Vector3d.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector3d obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
            context.separator();
            context.parameter("z", roundTo(obj.z, 5));
        }
    });
    public static final Formatter<Vector4f> VECTOR4F = REGISTRY.register(new Formatter<>(Vector4f.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/chat/component")) {
        @Override
        public void format(Vector4f obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x(), 5));
            context.separator();
            context.parameter("y", roundTo(obj.y(), 5));
            context.separator();
            context.parameter("z", roundTo(obj.z(), 5));
            context.separator();
            context.parameter("w", roundTo(obj.w(), 5));
        }
    });
    public static final Formatter<Pose> POSE = REGISTRY.register(new Formatter<>(Pose.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/pose")) {
        @Override
        public void format(Pose obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<MapColor> MAP_COLOR = REGISTRY.register(new Formatter<>(MapColor.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/map_color")) {
        @Override
        public void format(MapColor obj, IFormatterContext context) {
            Color color = new Color(FastColor.ARGB32.red(obj.col), FastColor.ARGB32.green(obj.col), FastColor.ARGB32.blue(obj.col));

            context.parameter("id", obj.id)
                    .separator();
            context.parameter("color", color);
        }
    });
    public static final Formatter<Block> BLOCK = REGISTRY.register(new Formatter<>(Block.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block")) {
        @Override
        public void format(Block obj, IFormatterContext context) {
            context.className("block")
                    .space()
                    .other(obj.arch$registryName());
        }
    });
    public static final Formatter<EntityType> ENTITY_TYPE = REGISTRY.register(new Formatter<>(EntityType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity_type")) {
        @Override
        public void format(EntityType obj, IFormatterContext context) {
            context.className("entity-type")
                    .space()
                    .other(obj.arch$registryName());
        }
    });
    public static final Formatter<BlockEntityType> BLOCK_ENTITY_TYPE = REGISTRY.register(new Formatter<>(BlockEntityType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block_entity_type")) {
        @Override
        public void format(BlockEntityType obj, IFormatterContext context) {
            context.className("block-entity-type")
                    .space()
                    .other(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(obj));
        }
    });
    public static final Formatter<Stat> STAT = REGISTRY.register(new Formatter<>(Stat.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(Stat obj, IFormatterContext context) {
            context.className("stat")
                    .space()
                    .string(obj.getName());
        }
    });
    public static final Formatter<StatType> STAT_TYPE = REGISTRY.register(new Formatter<>(StatType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(StatType obj, IFormatterContext context) {
            context.className("stat-type")
                    .space()
                    .other(BuiltInRegistries.STAT_TYPE.getKey(obj));
        }
    });
    public static final Formatter<VillagerProfession> VILLAGER_PROFESSION = REGISTRY.register(new Formatter<>(VillagerProfession.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/stat")) {
        @Override
        public void format(VillagerProfession obj, IFormatterContext context) {
            context.className("villager-profession")
                    .space()
                    .other(obj.name());
        }
    });
    public static final Formatter<Item> ITEM = REGISTRY.register(new Formatter<>(Item.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item")) {
        @Override
        public void format(Item obj, IFormatterContext context) {
            context.className("item")
                    .space()
                    .other(obj.arch$registryName());
        }
    });
    public static final Formatter<Rarity> RARITY = REGISTRY.register(new Formatter<>(Rarity.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/item/rarity")) {
        @Override
        public void format(Rarity obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<RenderShape> RENDER_SHAPE = REGISTRY.register(new Formatter<>(RenderShape.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/render_shape")) {
        @Override
        public void format(RenderShape obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<BlockBehaviour.OffsetType> BLOCK_OFFSET_TYPE = REGISTRY.register(new Formatter<>(BlockBehaviour.OffsetType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/block/offset_type")) {
        @Override
        public void format(BlockBehaviour.OffsetType obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<Difficulty> DIFFICULTY = REGISTRY.register(new Formatter<>(Difficulty.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/difficulty")) {
        @Override
        public void format(Difficulty obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<Direction> DIRECTION = REGISTRY.register(new Formatter<>(Direction.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/direction")) {
        @Override
        public void format(Direction obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<Axis> AXIS = REGISTRY.register(new Formatter<>(Axis.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/axis")) {
        @Override
        public void format(Axis obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<AxisDirection> AXIS_DIRECTION = REGISTRY.register(new Formatter<>(AxisDirection.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/axis_direction")) {
        @Override
        public void format(AxisDirection obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<Plane> PLANE = REGISTRY.register(new Formatter<>(Plane.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/plane")) {
        @Override
        public void format(Plane obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<Direction8> DIRECTION8 = REGISTRY.register(new Formatter<>(Direction8.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/direction_8")) {
        @Override
        public void format(Direction8 obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<FrontAndTop> FRONT_AND_TOP = REGISTRY.register(new Formatter<>(FrontAndTop.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/front_and_top")) {
        @Override
        public void format(FrontAndTop obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<InteractionHand> INTERACTION_HAND = REGISTRY.register(new Formatter<>(InteractionHand.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/interaction_hand")) {
        @Override
        public void format(InteractionHand obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<AABB> AABB = REGISTRY.register(new Formatter<>(AABB.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/interaction_hand")) {
        @Override
        public void format(AABB obj, IFormatterContext context) {
            context.parameter("min", List.of(roundTo(obj.minX, 3), roundTo(obj.minY, 3), roundTo(obj.minZ, 3))).operator(", ");
            context.parameter("max", List.of(roundTo(obj.maxX, 3), roundTo(obj.maxY, 3), roundTo(obj.maxZ, 3)));
        }
    });
    public static final Formatter<Env> DIST = REGISTRY.register(new Formatter<>(Env.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "forge/dist")) {
        @Override
        public void format(Env obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<MobSpawnType> MOB_SPAWN_TYPE = REGISTRY.register(new Formatter<>(MobSpawnType.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "minecraft/entity/mob_spawn_type")) {
        @Override
        public void format(MobSpawnType obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<MoonPhase> RT_MOON_PHASE = REGISTRY.register(new Formatter<>(MoonPhase.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "moon_phase")) {
        @Override
        public void format(MoonPhase obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<IFormattable> RT_FORMATTABLE = REGISTRY.register(new Formatter<>(IFormattable.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "formattable")) {
        @Override
        public void format(IFormattable obj, IFormatterContext context) {
            context.subFormat(obj::format);
        }
    });
    public static final Formatter<Angle> RT_ANGLE = REGISTRY.register(new Formatter<>(Angle.class, new ResourceLocation(IAdvancedDebug.get().getModId(), "angle")) {
        @Override
        public void format(Angle obj, IFormatterContext context) {
            context.subFormat(obj::format);
        }
    });

    public static void initClass() {
        
    }

    private ModDebugFormatters() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }
}
