package com.ultreon.mods.advanceddebug.client.menu

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.AdvancedDebug
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.Formatter
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui
import com.ultreon.mods.advanceddebug.api.common.*
import com.ultreon.mods.advanceddebug.api.events.InitPagesEvent
import com.ultreon.mods.advanceddebug.client.Config
import com.ultreon.mods.advanceddebug.client.formatter.FormatterContext
import com.ultreon.mods.advanceddebug.client.input.KeyBindingList
import com.ultreon.mods.advanceddebug.client.menu.pages.DefaultPage
import com.ultreon.mods.advanceddebug.client.registry.FormatterRegistry
import com.ultreon.mods.advanceddebug.mixin.common.KeyMappingAccessor
import com.ultreon.mods.advanceddebug.text.ComponentBuilder
import com.ultreon.mods.advanceddebug.util.InputUtils
import com.ultreon.mods.advanceddebug.util.memoize
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Renderable
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.lwjgl.glfw.GLFW
import org.slf4j.MarkerFactory
import java.util.*

/**
 * Client listener
 */
@Suppress("unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER")
class DebugGui private constructor() : GuiComponent(), Renderable, IDebugGui {
    private var font: Font? = null
    private val minecraft = Minecraft.getInstance()
    private var width = 0
    private var height = 0
    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (Minecraft.getInstance().options.renderDebug) {
            return
        }
        updateSize()
        font = Minecraft.getInstance().font
        val debugPage = debugPage
        val mc = Minecraft.getInstance()
        val scale = mc.window.guiScale
        val preferredScale: Double = if (Config.useCustomScale.get()) Config.customScale.get()?.toDouble() ?: scale else scale
        val useCustomScale = Config.useCustomScale.get()
        val rescaledWidth = (width.toDouble() * scale / preferredScale).toInt()
        val rescaledHeight = (height.toDouble() * scale / preferredScale).toInt()
        val context: DebugRenderContext = object : DebugRenderContext(poseStack, rescaledWidth, rescaledHeight) {
            override fun drawLine(pose: PoseStack, text: Component, x: Int, y: Int) {
                this@DebugGui.drawLine(pose, text, x, y)
            }
        }
        if (debugPage != null) {
            poseStack.pushPose()
            run {
                poseStack.scale((1 * preferredScale / scale).toFloat(), (1 * preferredScale / scale).toFloat(), 1f)
                val resourceLocation = debugPage.registryName()
                try {
                    if (Config.showCurrentPage.get()) {
                        drawLine(poseStack, Component.literal("Debug Page: $resourceLocation"), 6, (height * (scale / preferredScale) - 16).toInt())
                    }
                    debugPage.render(poseStack, context)
                } catch (e: Exception) {
                    AdvancedDebug.logger.error(
                        marker,
                        "Error rendering debug page {}",
                        resourceLocation,
                        e
                    )
                    try {
                        context.left(
                            Component.literal("Error rendering debug page $resourceLocation").withStyle(
                                ChatFormatting.RED
                            )
                        )
                    } catch (e1: Exception) {
                        AdvancedDebug.logger.error(marker, "Error showing error on debug page", e1)
                    }
                }
            }
            poseStack.popPose()
        } else {
            Companion.default.render(poseStack, context)
        }
    }

    private fun updateSize() {
        width = minecraft.window.guiScaledWidth
        height = minecraft.window.guiScaledHeight
    }

    private fun drawLine(pose: PoseStack, text: Component, x: Int, y: Int) {
        fill(pose, x, y, x + font!!.width(text) + 2, y - 1 + font!!.lineHeight + 2, 0x5f000000)
        font!!.draw(pose, text, (x + 1).toFloat(), (y + 1).toFloat(), 0xffffff)
    }

    val pages: List<DebugPage>
        get() = Collections.unmodifiableList(Companion.pages)

    override fun <T : DebugPage> registerPage(page: T): T {
        Companion.pages.add(page)
        require(!pageRegistry.containsKey(page.registryName())) { "Duplicate debug page registration: " + page.registryName() }
        pageRegistry[page.registryName()] = page
        return page
    }

    override val debugPage: DebugPage?
        get() {
            fixPage()
            return if (page == 0) {
                null
            } else Companion.pages[page - 1]
        }

    private fun fixPage() {
        page %= Companion.pages.size + 1
        if (page < 0) {
            page += Companion.pages.size + 1
        }
    }

    override var page = 0
        set(value) {
            field = value % (Companion.pages.size + 1)
        }

    override fun setPage(page: DebugPage) {
        require(Companion.pages.contains(page)) { "Page not registered." }
        this.page = Companion.pages.indexOf(page)
    }

    override fun next() {
        page++
    }

    override fun prev() {
        page--
    }

    fun onKeyReleased(keyCode: Int, scanCode: Int, action: Int, modifiers: Int): Boolean {
        if (action == GLFW.GLFW_RELEASE && keyCode == (KeyBindingList.DEBUG_SCREEN as KeyMappingAccessor).key.value) {
            if (InputUtils.isShiftDown) prev() else next()
            return true
        }
        return false
    }

    fun format(text: String, obj: Any?, vararg objects: Any?): Component {
        val sb = StringBuilder()
        val context = FormatterContext()
        format(obj, context)
        val builder = ComponentBuilder()
        builder.append(text, ChatFormatting.WHITE)
        builder.append(": ", ChatFormatting.GRAY)
        builder.append(context.build())
        for (`object` in objects) {
            val ctx = FormatterContext()
            format(`object`, ctx)
            builder.append(", ", ChatFormatting.GRAY)
            builder.append(ctx.build())
        }
        return builder.build()
    }

    override fun format(obj: Any?, context: IFormatterContext) {
        when (obj) {
            null -> {
                context.keyword("null")
            }
            is Class<*> -> {
                context.packageName(obj.getPackage().name + ".")
                context.className(obj.simpleName)
            }
            else -> {
                val identified: Formatter<*>? = formatterRegistry.identify(obj.javaClass)
                if (identified != null) {
                    formatUnsafe(identified, obj, context)
                    return
                }
                defaultFormatter.format(obj, context)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> formatUnsafe(formatter: Formatter<T>, obj: Any, context: IFormatterContext) {
        formatter.format(obj as T, context)
    }

    override val default: Formatter<Any>
        get() {
            return defaultFormatter
        }

    fun createInitEvent(): InitPagesEvent {
        return object : InitPagesEvent {
            override fun <T : DebugPage> register(page: T): T {
                return registerPage(page)
            }
        }
    }

    companion object {
        private val instance = DebugGui()
        private val pages: MutableList<DebugPage> = ArrayList()
        val default: DebugPage by memoize { DefaultPage() }

        private val formatterRegistry = FormatterRegistry.get()
        private val defaultFormatter: Formatter<Any> = object : Formatter<Any>(
            Any::class, AdvancedDebug.res("object")
        ) {
            override fun format(obj: Any, context: IFormatterContext) {
                val c: Class<*> = obj.javaClass
                context.classValue(c)
                context.identifier("@")
                context.hexValue(obj.hashCode())
            }
        }
        private val pageRegistry: MutableMap<ResourceLocation, DebugPage> = HashMap()
        private val marker = MarkerFactory.getMarker("DebugGui")
        @JvmStatic
        fun get(): DebugGui {
            return instance
        }
    }
}