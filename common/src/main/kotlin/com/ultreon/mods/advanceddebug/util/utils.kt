package com.ultreon.mods.advanceddebug.util

import com.google.common.base.Suppliers
import com.ultreon.mods.advanceddebug.AdvancedDebug
import dev.architectury.extensions.injected.InjectedRegistryEntryExtension
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun modRes(path: String): ResourceLocation {
    return AdvancedDebug.res(path)
}

val Int.color: Color get() = Color(this % 256, (this shr 2) % 256, (this shr 4) % 256)
val Int.colorWithAlpha: Color get() = Color(this % 256, (this shr 2) % 256, (this shr 4) % 256, (this shr 6) % 256)
val Vec3i.color: Color get() = Color(this.x, this.y, this.z)
val Vec3.color: Color get() = Color(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
val Vector3d.color: Color get() = Color(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())
val Vector3f.color: Color get() = Color(this.x(), this.y(), this.z())
val Vector4f.color: Color get() = Color(this.x(), this.y(), this.z(), this.w())

val InjectedRegistryEntryExtension<*>.registryName: ResourceLocation
    get() = this.`arch$registryName`()!!

fun <C, T>memoize(sup: () -> T): ReadOnlyProperty<C, T> {
    return Memoize(sup)
}

class Memoize<C, T>(sup: () -> T) :
    ReadOnlyProperty<C, T> {

    private val sup = Suppliers.memoize { sup() }
    override fun getValue(thisRef: C, property: KProperty<*>): T {
        return sup.get()
    }
}
