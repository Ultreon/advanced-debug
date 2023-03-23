package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player

class ItemPage(modId: String, name: String) : DebugPage(modId, name) {
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        if (Minecraft.getInstance().player != null) {
            val player: Player? = Minecraft.getInstance().player
            val stack = player!!.mainHandItem
            val item = stack.item
            val food = item.foodProperties
            if (stack.isEmpty) {
                ctx.top("<No Item Information>")
                return
            }
            ctx.left("Name")
            ctx.left("Internal Name", stack.item.`arch$registryName`())
            ctx.left("Item Type Name", stack.item.description.string)
            if (stack.hasCustomHoverName()) {
                ctx.left("Custom name", stack.hoverName.string)
            }
            ctx.left()
            ctx.left("Properties")
            ctx.left("Rarity", item.getRarity(stack))
            ctx.left("Enchantability", item.enchantmentValue)
            ctx.left("Stack Limit", item.maxStackSize)
            ctx.left("Max Damage", item.maxDamage)
            ctx.left("Damage", stack.damageValue)
            ctx.left("Count", stack.count)
            ctx.left("Repair Cost", stack.baseRepairCost)
            ctx.left("Use Duration", stack.useDuration)
            ctx.left("XP Repair Ratio", stack.baseRepairCost)
            ctx.left()
            if (food != null) {
                ctx.right("Misc")
                ctx.right("Food Properties")
                ctx.right("Food Hunger Points", food.nutrition)
                ctx.right("Food Saturation", food.saturationModifier)
                ctx.right()
            }
            ctx.right("Flags")
            ctx.right("Complex", item.isComplex)
            ctx.right("Immune To Fire", item.isFireResistant)
            ctx.right("Enchantable", item.isEnchantable(stack))
            ctx.right("Empty", stack.isEmpty)
            ctx.right("Stackable", stack.isStackable)
            ctx.right("Damageable", item.canBeDepleted())
            ctx.right("Damaged", stack.isDamaged)
            ctx.right()
        }
    }
}