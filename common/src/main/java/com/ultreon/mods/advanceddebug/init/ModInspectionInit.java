package com.ultreon.mods.advanceddebug.init;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Window;
import com.ultreon.data.types.*;
import com.ultreon.libs.commons.v0.tuple.Pair;
import com.ultreon.libs.commons.v0.tuple.Quadruple;
import com.ultreon.libs.commons.v0.tuple.Quintuple;
import com.ultreon.libs.commons.v0.tuple.Triple;
import com.ultreon.mods.advanceddebug.inspect.InspectionRoot;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluid;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ModInspectionInit {
    public static void registerAutoFillers() {
        InspectionRoot.registerAutoFill(Collection.class, node -> {
            node.create("size", Collection::size);
            node.createNode("items", Collection::toArray);
        });

        InspectionRoot.registerAutoFill(Map.class, node -> {
            node.create("size", Map::size);
            node.createNode("entries", mapType -> mapType.entrySet().toArray());
        });
        InspectionRoot.registerAutoFill(Map.Entry.class, node -> {
            try {
                node.create(InspectionRoot.format(node.getValue().getKey()), Map.Entry::getValue);
            } catch (Throwable e) {
                node.create("failure", e::getMessage);
            }
        });
        InspectionRoot.registerAutoFill(Entity.class, node -> {
            node.create("type", Entity::getType);
            node.create("x", Entity::getX);
            node.create("y", Entity::getY);
            node.create("z", Entity::getZ);
            node.create("airSupply", Entity::getAirSupply);
            node.create("maxAirSupply", Entity::getMaxAirSupply);
            node.create("maxAirSupply", Entity::getMaxAirSupply);
        });
        InspectionRoot.registerAutoFill(LivingEntity.class, node -> {
            node.createNode("armorCoverPercentage", LivingEntity::getActiveEffectsMap);
            node.createNode("offhandItem", LivingEntity::getOffhandItem);
            node.createNode("mainHandItem", LivingEntity::getMainHandItem);
            node.createNode("useItem", LivingEntity::getUseItem);
            node.create("useItemRemainTicks", LivingEntity::getUseItemRemainingTicks);
            node.create("health", LivingEntity::getHealth);
            node.create("maxHealth", LivingEntity::getMaxHealth);
            node.create("absorptionAmount", LivingEntity::getAbsorptionAmount);
            node.create("experienceReward", LivingEntity::getExperienceReward);
            node.create("armorValue", LivingEntity::getArmorValue);
            node.create("armorCoverPercentage", LivingEntity::getArmorCoverPercentage);
        });
        InspectionRoot.registerAutoFill(Player.class, node -> {
            node.createNode("gameProfile", Player::getGameProfile);
            node.createNode("foodData", Player::getFoodData);
            node.create("score", Player::getScore);
            node.create("name", Player::getName);
        });
        InspectionRoot.registerAutoFill(FoodData.class, node -> {
            node.create("food", FoodData::getFoodLevel);
            node.create("saturation", FoodData::getSaturationLevel);
            node.create("exhaustion", FoodData::getExhaustionLevel);
        });
        InspectionRoot.registerAutoFill(ServerData.class, node -> {
            node.create("isLan", ServerData::isLan);
        });
        InspectionRoot.registerAutoFill(MobEffectInstance.class, node -> {
            node.create("effect", MobEffectInstance::getEffect);
            node.create("amplifier", MobEffectInstance::getAmplifier);
            node.create("duration", MobEffectInstance::getDuration);
        });
        InspectionRoot.registerAutoFill(BlockEntity.class, node -> {
            node.createNode("state", BlockEntity::getBlockState);
            node.createNode("level", BlockEntity::getLevel);
            node.create("type", BlockEntity::getType);
            node.create("pos", BlockEntity::getBlockPos);
            node.create("removed", BlockEntity::isRemoved);
        });
        InspectionRoot.registerAutoFill(MinecraftServer.class, node -> {
            node.createNode("players", server -> server.getPlayerList().getPlayers().toArray());
            node.createNode("levels", server -> Lists.newArrayList(server.getAllLevels()).toArray());
            node.createNode("gameRules", MinecraftServer::getGameRules);
            node.create("tickCount", MinecraftServer::getTickCount);
        });
        InspectionRoot.registerAutoFill(Level.class, node -> {
            node.createNode("players", Level::players);
            node.create("day", Level::isDay);
            node.create("night", Level::isNight);
            node.create("raining", Level::isRaining);
            node.create("thundering", Level::isThundering);
            node.create("isDebug", Level::isDebug);
            node.create("dayTime", Level::getDayTime);
            node.create("gameTime", Level::getGameTime);
            node.create("height", Level::getHeight);
            node.create("seaLevel", Level::getSeaLevel);
        });
        InspectionRoot.registerAutoFill(ServerLevel.class, node -> {
            node.createNode("entities", level -> Lists.newArrayList(level.getAllEntities()).toArray());
            node.createNode("dragons", ServerLevel::getDragons);
            node.create("isFlat", ServerLevel::isFlat);
            node.create("handlingTick", ServerLevel::isHandlingTick);
            node.create("canSleepThroughNights", ServerLevel::canSleepThroughNights);
        });
        InspectionRoot.registerAutoFill(ServerLevel.class, node -> {
            node.createNode("entities", level -> Lists.newArrayList(level.getAllEntities()).toArray());
            node.createNode("dragons", ServerLevel::getDragons);
            node.create("isFlat", ServerLevel::isFlat);
            node.create("handlingTick", ServerLevel::isHandlingTick);
            node.create("canSleepThroughNights", ServerLevel::canSleepThroughNights);
        });
        InspectionRoot.registerAutoFill(Window.class, node -> {
            node.create("handle", Window::getWindow);
            node.create("fpsLimit", Window::getFramerateLimit);
            node.create("guiScale", Window::getGuiScale);
            node.create("scaledWidth", Window::getGuiScaledWidth);
            node.create("scaledHeight", Window::getGuiScaledHeight);
            node.create("refreshRate", Window::getRefreshRate);
        });
        InspectionRoot.registerAutoFill(GameProfile.class, node -> {
            node.create("id", GameProfile::getId);
            node.create("name", GameProfile::getName);
        });
        InspectionRoot.registerAutoFill(Inventory.class, node -> {
            node.create("items", element -> element.items);
            node.create("armor", element -> element.armor);
        });
        InspectionRoot.registerAutoFill(ItemStack.class, node -> {
            node.createNode("tag", ItemStack::getTag);
            node.create("item", ItemStack::getItem);
            node.create("count", ItemStack::getCount);
            node.create("damageValue", ItemStack::getDamageValue);
        });
        InspectionRoot.registerAutoFill(CompoundTag.class, node -> {
            node.create("size", CompoundTag::size);
            node.createNode("entries", mapType -> mapType.getAllKeys().stream().map(key -> Map.entry(key, mapType.get(key))).toArray());
        });
        InspectionRoot.registerAutoFill(ListTag.class, node -> {
            node.create("size", ListTag::size);
            node.createNode("items", AbstractCollection::toArray);
        });
        InspectionRoot.registerAutoFill(LocalPlayer.class, node -> {
            node.create("currentMood", LocalPlayer::getCurrentMood);
            node.create("waterVision", LocalPlayer::getWaterVision);
            node.create("visualRotYInDeg", LocalPlayer::getVisualRotationYInDegrees);
            node.create("spinningFxIntensity", element -> element.spinningEffectIntensity);
        });
    }

    public void registerFormatters() {
        InspectionRoot.registerFormatter(Entity.class, element -> "Entity: " + element.getId());
        InspectionRoot.registerFormatter(MobEffect.class, element -> "Mob Effect: " + BuiltInRegistries.MOB_EFFECT.getKey(element));
        InspectionRoot.registerFormatter(Block.class, element -> "Block: " + BuiltInRegistries.BLOCK.getKey(element));
        InspectionRoot.registerFormatter(Item.class, element -> "Item: " + BuiltInRegistries.ITEM.getKey(element));
        InspectionRoot.registerFormatter(ItemLike.class, element -> "Item Like: " + BuiltInRegistries.ITEM.getKey(element.asItem()));
        InspectionRoot.registerFormatter(EntityType.class, element -> "Item Like: " + BuiltInRegistries.ENTITY_TYPE.getKey(element));
        InspectionRoot.registerFormatter(Enchantment.class, element -> "Enchantment: " + BuiltInRegistries.ENCHANTMENT.getKey(element));
        InspectionRoot.registerFormatter(Potion.class, element -> "Enchantment: " + BuiltInRegistries.POTION.getKey(element));
        InspectionRoot.registerFormatter(PoiType.class, element -> "POI Type: " + BuiltInRegistries.POINT_OF_INTEREST_TYPE.getKey(element));
        InspectionRoot.registerFormatter(Attribute.class, element -> "Attribute: " + BuiltInRegistries.ATTRIBUTE.getKey(element));
        InspectionRoot.registerFormatter(Activity.class, element -> "Activity: " + BuiltInRegistries.ACTIVITY.getKey(element));
        InspectionRoot.registerFormatter(BannerPattern.class, element -> "Banner Pattern: " + BuiltInRegistries.BANNER_PATTERN.getKey(element));
        InspectionRoot.registerFormatter(Fluid.class, element -> "Fluid: " + BuiltInRegistries.FLUID.getKey(element));
        InspectionRoot.registerFormatter(CatVariant.class, element -> "Cat Variant: " + BuiltInRegistries.CAT_VARIANT.getKey(element));
        InspectionRoot.registerFormatter(Feature.class, element -> "WorldGen Feature: " + BuiltInRegistries.FEATURE.getKey(element));
        InspectionRoot.registerFormatter(PlacementModifierType.class, element -> "Placement Modifier Type: " + BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.getKey(element));
        InspectionRoot.registerFormatter(PlacementModifier.class, element -> "Placement Modifier: " + BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.getKey(element.type()));

        InspectionRoot.registerFormatter(Pair.class, element -> element.getFirst() + "," + element.getSecond());
        InspectionRoot.registerFormatter(Triple.class, element -> element.getFirst() + "," + element.getSecond() + "," + element.getThird());
        InspectionRoot.registerFormatter(Quadruple.class, element -> element.getFirst() + "," + element.getSecond() + "," + element.getThird() + "," + element.getFourth());
        InspectionRoot.registerFormatter(Quintuple.class, element -> element.getFirst() + "," + element.getSecond() + "," + element.getThird() + "," + element.getFourth() + "," + element.getFifth());
        InspectionRoot.registerFormatter(Enum.class, Enum::name);
        InspectionRoot.registerFormatter(ByteType.class, byteType -> Byte.toString(byteType.getValue()));
        InspectionRoot.registerFormatter(ShortType.class, shortType -> Short.toString(shortType.getValue()));
        InspectionRoot.registerFormatter(IntType.class, intType -> Integer.toString(intType.getValue()));
        InspectionRoot.registerFormatter(LongType.class, longType -> Long.toString(longType.getValue()));
        InspectionRoot.registerFormatter(FloatType.class, floatType -> Float.toString(floatType.getValue()));
        InspectionRoot.registerFormatter(DoubleType.class, doubleType -> Double.toString(doubleType.getValue()));
        InspectionRoot.registerFormatter(BooleanType.class, booleanType -> Boolean.toString(booleanType.getValue()));
        InspectionRoot.registerFormatter(CharType.class, charType -> Character.toString(charType.getValue()));
        InspectionRoot.registerFormatter(StringType.class, stringType -> "\"" + stringType.getValue() + "\"");
        InspectionRoot.registerFormatter(UUIDType.class, uuidType -> uuidType.getValue().toString());

        InspectionRoot.registerFormatter(ByteTag.class, byteType -> Byte.toString(byteType.getAsByte()));
        InspectionRoot.registerFormatter(ShortTag.class, shortType -> Short.toString(shortType.getAsShort()));
        InspectionRoot.registerFormatter(IntTag.class, intType -> Integer.toString(intType.getAsInt()));
        InspectionRoot.registerFormatter(LongTag.class, longType -> Long.toString(longType.getAsLong()));
        InspectionRoot.registerFormatter(FloatTag.class, floatType -> Float.toString(floatType.getAsFloat()));
        InspectionRoot.registerFormatter(DoubleTag.class, doubleType -> Double.toString(doubleType.getAsDouble()));
        InspectionRoot.registerFormatter(StringTag.class, stringType -> "\"" + stringType.getAsString() + "\"");
        InspectionRoot.registerFormatter(Player.class, player -> player.getName() + " (" + player.getUUID() + ")");
        InspectionRoot.registerFormatter(ServerPlayer.class, player -> player.getName() + " (" + player.getUUID() + ")");
        InspectionRoot.registerFormatter(LocalPlayer.class, player -> player.getName() + " (" + player.getUUID() + ")");
        InspectionRoot.registerFormatter(RemotePlayer.class, player -> player.getName() + " (" + player.getUUID() + ")");
        InspectionRoot.registerFormatter(AbstractClientPlayer.class, player -> player.getName() + " (" + player.getUUID() + ")");
    }
}
