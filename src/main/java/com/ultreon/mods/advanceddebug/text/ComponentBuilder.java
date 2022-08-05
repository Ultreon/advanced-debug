package com.ultreon.mods.advanceddebug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.*;

import java.awt.*;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnusedReturnValue")
public class ComponentBuilder {
    private final MutableComponent component;

    public ComponentBuilder() {
        this.component = new TextComponent("");
    }

    public ComponentBuilder(MutableComponent component) {
        this.component = component;
    }

    public ComponentBuilder append(Component component) {
        this.component.append(component);
        return this;
    }

    public ComponentBuilder append(String text) {
        this.component.append(text);
        return this;
    }

    public ComponentBuilder append(String text, ChatFormatting... formatting) {
        this.component.append(new TextComponent(text).withStyle(formatting));
        return this;
    }

    public ComponentBuilder append(String text, ChatFormatting formatting) {
        this.component.append(new TextComponent(text).withStyle(formatting));
        return this;
    }

    public ComponentBuilder append(String text, Style style) {
        this.component.append(new TextComponent(text).withStyle(style));
        return this;
    }

    public ComponentBuilder append(String text, UnaryOperator<Style> unaryOperator) {
        this.component.append(new TextComponent(text).withStyle(unaryOperator));
        return this;
    }

    public ComponentBuilder append(ComponentBuilder otherBuilder) {
        this.component.append(otherBuilder.build());
        return this;
    }

    public ComponentBuilder colored(String text, String s) {
        if (!s.startsWith("#")) {
            s = "#" + s;
        }
        String finalS = s;
        this.component.append(new TextComponent(text).withStyle(style -> style.withColor(TextColor.fromRgb(Color.decode(finalS).getRGB()))));
        return this;
    }

    public Component build() {
        return this.component;
    }
}
