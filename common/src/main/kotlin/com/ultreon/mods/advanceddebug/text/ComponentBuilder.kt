package com.ultreon.mods.advanceddebug.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.awt.Color
import java.util.function.UnaryOperator

class ComponentBuilder {
    private val component: MutableComponent

    constructor() {
        component = Component.literal("")
    }

    constructor(component: MutableComponent) {
        this.component = component
    }

    fun append(component: Component): ComponentBuilder {
        this.component.append(component)
        return this
    }

    fun append(text: String): ComponentBuilder {
        component.append(text)
        return this
    }

    fun append(text: String, vararg formatting: ChatFormatting): ComponentBuilder {
        component.append(Component.literal(text).withStyle(*formatting))
        return this
    }

    fun append(text: String, formatting: ChatFormatting): ComponentBuilder {
        component.append(Component.literal(text).withStyle(formatting))
        return this
    }

    fun append(text: String, style: Style): ComponentBuilder {
        component.append(Component.literal(text).withStyle(style))
        return this
    }

    fun append(text: String, unaryOperator: UnaryOperator<Style>): ComponentBuilder {
        component.append(Component.literal(text).withStyle(unaryOperator))
        return this
    }

    fun append(otherBuilder: ComponentBuilder): ComponentBuilder {
        component.append(otherBuilder.build())
        return this
    }

    fun colored(text: String, hex: String): ComponentBuilder {
        var color = hex
        if (!color.startsWith("#")) {
            color = "#$color"
        }
        val finalS = color
        component.append(
            Component.literal(text)
                .withStyle { style: Style -> style.withColor(TextColor.fromRgb(Color.decode(finalS).rgb)) })
        return this
    }

    fun build(): Component {
        return component
    }
}

val MutableComponent.builder get() = ComponentBuilder(this)
