package com.ultreon.mods.advanceddebug.client.menu.pages

import com.mojang.blaze3d.vertex.PoseStack
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext
import com.ultreon.mods.advanceddebug.api.common.IntSize
import com.ultreon.mods.advanceddebug.mixin.common.WindowAccessor
import org.lwjgl.glfw.GLFW

class ComputerPage(modId: String, name: String) : DebugPage(modId, name) {
    private val window = minecraft.window
    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun render(poseStack: PoseStack, ctx: IDebugRenderContext) {
        val l = GLFW.glfwGetWindowMonitor(window.window)
        (window as WindowAccessor).screenManager?.getMonitor(l)?.let { monitor ->
            val currentMode = monitor.currentMode
            ctx.left("Monitor")
            ctx.left("Screen Size", IntSize(currentMode.width, currentMode.height))
            ctx.left("Screen Pos", IntSize(monitor.x, monitor.y))
            ctx.left("Refresh Rate", currentMode.refreshRate)
            ctx.left("RGB Bits", currentMode.redBits, currentMode.greenBits, currentMode.blueBits)
            ctx.left("Bits", currentMode.redBits + currentMode.greenBits + currentMode.blueBits)
            ctx.left()
        }

        runCatching {
            ctx.left("System Info")
            ctx.left("OS Version", System.getProperty("os.version"))
            ctx.left("OS Name", System.getProperty("os.name"))

            runCatching { ctx.left("OS Architecture", System.getProperty("os.arch")) }
            runCatching { ctx.left("Java Version", System.getProperty("java.version")) }
            runCatching { ctx.left("Java Vendor", System.getProperty("java.vendor")) }
            runCatching { ctx.left("Java VM Version", System.getProperty("java.vm.version")) }
            runCatching { ctx.left("Java VM Vendor", System.getProperty("java.vm.vendor")) }
            runCatching { ctx.left("Java VM Name", System.getProperty("java.vm.name")) }
            runCatching { ctx.left("Java Class Version", System.getProperty("java.class.version")) }
            runCatching { ctx.left("Java Compiler", System.getProperty("java.compiler")) }

            ctx.left()
        }

        ctx.right("Is Java 64-bit", if (minecraft.is64Bit) "yes" else "no")
    }
}