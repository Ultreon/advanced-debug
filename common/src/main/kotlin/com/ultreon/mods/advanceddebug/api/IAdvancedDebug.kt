package com.ultreon.mods.advanceddebug.api

import com.ultreon.mods.advanceddebug.api.client.menu.IDebugGui
import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry
import java.lang.reflect.InvocationTargetException

interface IAdvancedDebug {
    val gui: IDebugGui
    val formatterRegistry: IFormatterRegistry
    val isSpacedNamespace: Boolean
    val isSpacedEnumConstants: Boolean
    fun enableBubbleBlasterID(): Boolean

    companion object {
        fun get(): IAdvancedDebug {
            return try {
                Class.forName("AdvancedDebug").getDeclaredMethod("getInstance")(null) as IAdvancedDebug
            } catch (e: IllegalAccessException) {
                throw IllegalStateException("Can't get instance object of the Advanced Debug Mod.")
            } catch (e: InvocationTargetException) {
                throw IllegalStateException("Can't get instance object of the Advanced Debug Mod.")
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException("Can't get instance object of the Advanced Debug Mod.")
            } catch (e: ClassNotFoundException) {
                throw IllegalStateException("Can't get instance object of the Advanced Debug Mod.")
            }
        }
    }
}