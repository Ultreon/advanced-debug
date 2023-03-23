package com.ultreon.mods.advanceddebug.api.init

import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.client.menu.formatter
import com.ultreon.mods.advanceddebug.api.common.Angle
import com.ultreon.mods.advanceddebug.api.common.IFormattable
import com.ultreon.mods.advanceddebug.api.common.MoonPhase
import com.ultreon.mods.advanceddebug.api.util.MathUtil
import com.ultreon.mods.advanceddebug.util.modRes
import dev.architectury.utils.Env
import net.minecraft.advancements.Advancement
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatType
import net.minecraft.util.FastColor
import net.minecraft.world.Difficulty
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color
import java.util.*

@Suppress("unused")
class ModDebugFormatters private constructor() {
    init {
        throw UnsupportedOperationException("Cannot instantiate a utility class")
    }

    companion object {
        private val REGISTRY = AdvancedDebug.formatterRegistry
        val NUMBER = REGISTRY.register(
            formatter(Number::class, modRes("java/number")) { obj, context ->
                context.number(obj)
            }
        )
        val K_BOOLEAN = REGISTRY.register(
            formatter(Boolean::class, modRes("kotlin/boolean")) { obj, context ->
                context.keyword(if (obj) "true" else "false")
            }
        )
        val BOOLEAN = REGISTRY.register(
            formatter(java.lang.Boolean::class, modRes("java/boolean")) { obj, context ->
                context.keyword(if (obj.booleanValue()) "true" else "false")
            }
        )
        val STRING = REGISTRY.register(
            formatter(String::class, modRes("java/string")) { obj, context ->
                context.string("\"").stringEscaped(obj).string("\"")
            }
        )
        val CHARACTER = REGISTRY.register(
            formatter(Char::class, modRes("java/character")) { obj, context ->
                context.string("'").charsEscaped(obj.toString()).string("'")
            }
        )
        val ENUM = REGISTRY.register(
            formatter(Enum::class, modRes("java/enum")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val LIST = REGISTRY.register(
            formatter(List::class, modRes("java/list")) { obj, context ->
                context.operator("[")
                val it = obj.iterator()
                if (!it.hasNext()) {
                    context.operator("]")
                    return@formatter
                }
                while (true) {
                    val e = it.next()!!
                    if (e === obj) {
                        context.keyword("[...]")
                    } else {
                        context.other(e)
                    }
                    if (!it.hasNext()) {
                        context.operator("]")
                        return@formatter
                    }
                    context.separator()
                }
            }
        )
        val MAP = REGISTRY.register(
            formatter(Map::class, modRes("java/map")) { obj, context ->
                context.operator("{")
                val it = obj.entries.iterator()
                if (!it.hasNext()) {
                    context.operator("}")
                    return@formatter
                }
                while (true) {
                    val e = it.next()
                    if (e.key === obj) {
                        context.keyword("{...}")
                    } else if (e.key === e) {
                        context.keyword("<...>")
                    } else {
                        context.other(e.key)
                    }
                    context.operator(":")
                    if (e.value === obj) {
                        context.keyword("{...}")
                    } else if (e.value === e) {
                        context.keyword("<...>")
                    } else {
                        context.other(e.value)
                    }
                    if (!it.hasNext()) {
                        context.operator("}")
                        return@formatter
                    }
                    context.separator()
                }
            }
        )
        val MAP_ENTRY = REGISTRY.register(
            formatter(Map.Entry::class, modRes("java/map/entry")) { obj, context ->
                if (obj.key === obj) {
                    context.keyword("<...>")
                } else {
                    context.other(obj.key)
                }
                context.operator(": ")
                if (obj.value === obj) {
                    context.keyword("<...>")
                } else {
                    context.other(obj.value)
                }
            }
        )
        val SET = REGISTRY.register(
            formatter(Set::class, modRes("java/set")) { obj, context ->
                context.operator("{")
                val it = obj.iterator()
                if (!it.hasNext()) {
                    context.operator("}")
                    return@formatter
                }
                while (true) {
                    val e = it.next()!!
                    if (e === obj) {
                        context.keyword("{...}")
                    } else {
                        context.other(e)
                    }
                    if (!it.hasNext()) {
                        context.operator("}")
                        return@formatter
                    }
                    context.separator()
                }
            }
        )
        val COLLECTION = REGISTRY.register(
            formatter(Collection::class, modRes("java/set")) { obj, context ->
                context.operator("(")
                val it = obj.iterator()
                if (!it.hasNext()) {
                    context.operator(")")
                    return@formatter
                }
                while (true) {
                    val e = it.next()!!
                    if (e === obj) {
                        context.keyword("(...)")
                    } else {
                        context.other(e)
                    }
                    if (!it.hasNext()) {
                        context.operator(")")
                        return@formatter
                    }
                    context.separator()
                }
            }
        )
        val UUID = REGISTRY.register(
            formatter<UUID>(UUID::class, modRes("java/uuid")) { obj, context ->
                val iterator = Arrays.stream(obj.toString().split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()).iterator()
                if (!iterator.hasNext()) return@formatter
                while (true) {
                    val s = iterator.next()
                    context.hex(s)
                    if (!iterator.hasNext()) return@formatter
                    context.operator("-")
                }
            }
        )
        val AWT_COLOR = REGISTRY.register(
            formatter(Color::class, modRes("java/awt/color")) { obj, context ->
                val s = Integer.toHexString(obj.rgb)
                context.operator("#")
                context.hex("0".repeat(8 - s.length) + s)
            }
        )
        val ENTITY = REGISTRY.register(
            formatter(Entity::class, modRes("minecraft/entity")) { obj, context ->
                context.className(obj.javaClass.name).space().other(obj.uuid)
                context.operator(" (").other(obj.displayName).operator(") #").other(obj.id)
            }
        )
        val PLAYER = REGISTRY.register(
            formatter(Player::class, modRes("minecraft/player")) { obj, context ->
                context.className("Player").space().other(obj.uuid)
                context.operator(" (").other(obj.gameProfile.name).operator(")")
            }
        )
        val RESOURCE_LOCATION = REGISTRY.register(
            formatter(
                ResourceLocation::class,
                modRes("minecraft/resource_location")
            ) { obj, context ->
                if (AdvancedDebug.isSpacedNamespace) {
                    context.operator("(")
                        .className(obj.namespace.replace("_".toRegex(), " "))
                        .operator(") ")
                        .identifier(obj.path.replace("_".toRegex(), " ").replace("/".toRegex(), " -> "))
                } else if (AdvancedDebug.enableBubbleBlasterID()) {
                    var namespace = obj.namespace
                    if (namespace == ResourceLocation.DEFAULT_NAMESPACE) namespace = "bubbles"
                    context.identifier(obj.path)
                        .operator("@")
                        .className(namespace.replace("_".toRegex(), "."))
                } else {
                    context.className(obj.namespace)
                        .operator(":")
                        .identifier(obj.path)
                }
            }
        )
        val ITEM_STACK = REGISTRY.register(
            formatter(ItemStack::class, modRes("minecraft/item_stack")) { obj, context ->
                context.intValue(obj.count)
                    .operator("× ")
                context.other(obj.item.`arch$registryName`())
            }
        )
        val CHAT_COMPONENT = REGISTRY.register(
            formatter(Component::class, modRes("minecraft/chat/component")) { obj, context ->
                context.className(obj.javaClass.simpleName)
                    .space()
                    .string("\"")
                    .stringEscaped(obj.string)
                    .string("\"")
            }
        )
        val MUTABLE_COMPONENT = REGISTRY.register(
            formatter(
                MutableComponent::class,
                modRes("minecraft/chat/component")
            ) { obj, context ->
                context.className("mutable component")
                    .space()
                    .other(obj.string)
            }
        )
        val VEC2 = REGISTRY.register(
            formatter(Vec2::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", MathUtil.roundTo(obj.x, 5))
                context.separator()
                context.parameter("y", MathUtil.roundTo(obj.y, 5))
            }
        )
        val VEC3 = REGISTRY.register(
            formatter(Vec3::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", MathUtil.roundTo(obj.x, 5))
                context.separator()
                context.parameter("y", MathUtil.roundTo(obj.y, 5))
                context.separator()
                context.parameter("z", MathUtil.roundTo(obj.z, 5))
            }
        )
        val VEC3I = REGISTRY.register(
            formatter(Vec3i::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", obj.x)
                context.separator()
                context.parameter("y", obj.y)
                context.separator()
                context.parameter("z", obj.z)
            }
        )
        val VECTOR3F = REGISTRY.register(
            formatter(Vector3f::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", MathUtil.roundTo(obj.x(), 5))
                context.separator()
                context.parameter("y", MathUtil.roundTo(obj.y(), 5))
                context.separator()
                context.parameter("z", MathUtil.roundTo(obj.z(), 5))
            }
        )
        val VECTOR3D = REGISTRY.register(
            formatter(Vector3d::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", MathUtil.roundTo(obj.x, 5))
                context.separator()
                context.parameter("y", MathUtil.roundTo(obj.y, 5))
                context.separator()
                context.parameter("z", MathUtil.roundTo(obj.z, 5))
            }
        )
        val VECTOR4F = REGISTRY.register(
            formatter(Vector4f::class, modRes("minecraft/chat/component")) { obj, context ->
                context.parameter("x", MathUtil.roundTo(obj.x(), 5))
                context.separator()
                context.parameter("y", MathUtil.roundTo(obj.y(), 5))
                context.separator()
                context.parameter("z", MathUtil.roundTo(obj.z(), 5))
                context.separator()
                context.parameter("w", MathUtil.roundTo(obj.w(), 5))
            }
        )
        val POSE = REGISTRY.register(
            formatter(Pose::class, modRes("minecraft/pose")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val MATERIAL_COLOR = REGISTRY.register(
            formatter(MaterialColor::class, modRes("minecraft/material_color")) { obj, context ->
                val color = Color(
                    FastColor.ARGB32.red(obj.col),
                    FastColor.ARGB32.green(obj.col),
                    FastColor.ARGB32.blue(obj.col)
                )
                context.parameter("id", obj.id)
                    .separator()
                context.parameter("color", color)
            }
        )
        val BLOCK = REGISTRY.register(
            formatter(Block::class, modRes("minecraft/block")) { obj, context ->
                context.className("block")
                    .space()
                    .other(obj.`arch$registryName`())
            }
        )
        val ENTITY_TYPE = REGISTRY.register(
            formatter(EntityType::class, modRes("minecraft/entity_type")) { obj, context ->
                context.className("entity-type")
                    .space()
                    .other(obj.`arch$registryName`())
            }
        )
        val BLOCK_ENTITY_TYPE = REGISTRY.register(
            formatter(
                BlockEntityType::class,
                modRes("minecraft/block_entity_type")
            ) { obj, context ->
                context.className("block-entity-type")
                    .space()
                    .other(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(obj))
            }
        )
        val ADVANCEMENT = REGISTRY.register(
            formatter(Advancement::class, modRes("minecraft/advancement")) { obj, context ->
                context.className("advancement")
                    .space()
                    .other(obj.id)
            }
        )
        val STAT = REGISTRY.register(
            formatter(Stat::class, modRes("minecraft/stat")) { obj, context ->
                context.className("stat")
                    .space()
                    .string(obj.name)
            }
        )
        val STAT_TYPE = REGISTRY.register(
            formatter(StatType::class, modRes("minecraft/stat")) { obj, context ->
                context.className("stat-type")
                    .space()
                    .other(BuiltInRegistries.STAT_TYPE.getKey(obj))
            }
        )
        val VILLAGER_PROFESSION = REGISTRY.register(
            formatter(VillagerProfession::class, modRes("minecraft/stat")) { obj, context ->
                context.className("villager-profession")
                    .space()
                    .other(obj.name())
            }
        )
        val ITEM = REGISTRY.register(
            formatter(Item::class, modRes("minecraft/item")) { obj, context ->
                context.className("item")
                    .space()
                    .other(obj.`arch$registryName`())
            }
        )
        val RARITY = REGISTRY.register(
            formatter(Rarity::class, modRes("minecraft/item/rarity")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val RENDER_SHAPE = REGISTRY.register(
            formatter(RenderShape::class, modRes("minecraft/render_shape")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val BLOCK_OFFSET_TYPE = REGISTRY.register(
            formatter(
                BlockBehaviour.OffsetType::class,
                modRes("minecraft/block/offset_type")
            ) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val DIFFICULTY = REGISTRY.register(
            formatter(Difficulty::class, modRes("minecraft/difficulty")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val DIRECTION = REGISTRY.register(
            formatter(Direction::class, modRes("minecraft/direction")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val AXIS = REGISTRY.register(
            formatter(Direction.Axis::class, modRes("minecraft/axis")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val AXIS_DIRECTION = REGISTRY.register(
            formatter(
                Direction.AxisDirection::class,
                modRes("minecraft/axis_direction")
            ) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val PLANE = REGISTRY.register(
            formatter(Direction.Plane::class, modRes("minecraft/plane")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val DIRECTION8 = REGISTRY.register(
            formatter(Direction8::class, modRes("minecraft/direction_8")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val FRONT_AND_TOP = REGISTRY.register(
            formatter(FrontAndTop::class, modRes("minecraft/front_and_top")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val INTERACTION_HAND = REGISTRY.register(
            formatter(
                InteractionHand::class,
                modRes("minecraft/interaction_hand")
            ) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val AABB = REGISTRY.register(
            formatter(
                net.minecraft.world.phys.AABB::class,
                modRes("minecraft/interaction_hand")
            ) { obj, context ->
                context.parameter(
                    "min",
                    listOf(
                        MathUtil.roundTo(obj.minX, 3),
                        MathUtil.roundTo(obj.minY, 3),
                        MathUtil.roundTo(obj.minZ, 3)
                    )
                ).operator(", ")
                context.parameter(
                    "max",
                    listOf(
                        MathUtil.roundTo(obj.maxX, 3),
                        MathUtil.roundTo(obj.maxY, 3),
                        MathUtil.roundTo(obj.maxZ, 3)
                    )
                )
            }
        )
        val DIST = REGISTRY.register(
            formatter(Env::class, modRes("forge/dist")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val MOB_SPAWN_TYPE = REGISTRY.register(
            formatter(
                MobSpawnType::class,
                modRes("minecraft/entity/mob_spawn_type")
            ) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val RT_MOON_PHASE = REGISTRY.register(
            formatter(MoonPhase::class, modRes("moon_phase")) { obj, context ->
                context.enumConstant(obj)
            }
        )
        val RT_FORMATTABLE = REGISTRY.register(
            formatter(IFormattable::class, modRes("formattable")) { obj, context ->
                context.subFormat { ctx: IFormatterContext? ->
                    obj.format(
                        ctx!!
                    )
                }
            }
        )
        val RT_ANGLE = REGISTRY.register(
            formatter(Angle::class, modRes("angle")) { obj, context ->
                context.subFormat { ctx: IFormatterContext? ->
                    obj.format(
                        ctx!!
                    )
                }
            }
        )

        @JvmStatic
        fun initClass() {
        }
    }
}