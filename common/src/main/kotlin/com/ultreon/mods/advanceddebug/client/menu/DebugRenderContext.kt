package com.ultreon.mods.advanceddebug.client.menu

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

abstract class DebugRenderContext(private val pose: PoseStack, val width: Int, val height: Int) : IDebugRenderContext {
    private var left = 0
    private var top = 0
    private var right = 0
    private val mc = Minecraft.getInstance()

    override fun left(text: Component, `object`: Any?, vararg objects: Any?) {
        drawLeft(pose, text.string, left++, `object`, *objects)
    }

    override fun left(text: String, `object`: Any?, vararg objects: Any?) {
        drawLeft(pose, text, left++, `object`, *objects)
    }

    override fun left(text: Component) {
        drawLeft(pose, text.string, left++)
    }

    override fun left(text: String) {
        drawLeft(pose, ChatFormatting.GRAY.toString() + "-== " + text + ChatFormatting.GRAY + " ==-", left++)
    }

    override fun left() {
        left++
    }

    override fun right(text: Component, `object`: Any?, vararg objects: Any?) {
        drawRight(pose, text.string, right++, `object`, *objects)
    }

    override fun right(text: String, `object`: Any?, vararg objects: Any?) {
        drawRight(pose, text, right++, `object`, *objects)
    }

    override fun right(text: Component) {
        drawRight(pose, text.string, right++)
    }

    override fun right(text: String) {
        drawRight(pose, ChatFormatting.GRAY.toString() + "-== " + text + ChatFormatting.GRAY + " ==-", right++)
    }

    override fun right() {
        right++
    }

    override fun top(text: Component) {
        drawTop(pose, text.string, top++)
    }

    override fun top(text: String) {
        drawTop(pose, text, top++)
    }

    override fun top() {
        top++
    }

    private fun drawTop(pose: PoseStack, text: String, line: Int) {
        drawLine(
            pose,
            Component.literal(text),
            (width / 2f - mc.font.width(text) / 2f).toInt(),
            VERTICAL_OFFSET + line * (mc.font.lineHeight + 2)
        )
    }

    private fun drawLeft(pose: PoseStack, text: String, line: Int, obj: Any?, vararg objects: Any?) {
        drawLine(
            pose,
            DebugGui.get().format(text, obj, *objects),
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET + line * (mc.font.lineHeight + 2)
        )
    }

    private fun drawLeft(pose: PoseStack, text: String, line: Int) {
        drawLine(pose, Component.literal(text), HORIZONTAL_OFFSET, VERTICAL_OFFSET + line * (mc.font.lineHeight + 2))
    }

    private fun drawRight(pose: PoseStack, text: String, line: Int, obj: Any?, vararg objects: Any?) {
        val format: Component = DebugGui.get().format(text, obj, *objects)
        drawLine(
            pose,
            format,
            width - HORIZONTAL_OFFSET - mc.font.width(format),
            VERTICAL_OFFSET + line * (mc.font.lineHeight + 2)
        )
    }

    private fun drawRight(pose: PoseStack, text: String, line: Int) {
        drawLine(
            pose,
            Component.literal(text),
            width - HORIZONTAL_OFFSET - mc.font.width(text),
            VERTICAL_OFFSET + line * (mc.font.lineHeight + 2)
        )
    }

    protected abstract fun drawLine(pose: PoseStack, text: Component, x: Int, y: Int)

    companion object {
        private const val HORIZONTAL_OFFSET = 6
        private const val VERTICAL_OFFSET = 6
    }
}