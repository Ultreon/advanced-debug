package com.ultreon.mods.advanceddebug.client.formatter;

import com.ultreon.mods.advanceddebug.api.IAdvancedDebug;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.client.Config;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.Consumer;

public class FormatterContext implements IFormatterContext {
    public static final String KEYWORD_COLOR = "#EA32FF";
    public static final String NUMBER_COLOR = "#FF7632";
    public static final String ENUM_CONST_COLOR = "#FF7632";
    public static final String STRING_COLOR = "#5FFF32";
    public static final String STRING_ESCAPE_COLOR = "#32F4FF";
    public static final String METHOD_COLOR = "#32A3FF";
    public static final String FIELD_COLOR = "#FF3251";
    public static final String CLASS_COLOR = "#FFC832";
    public static final String COMMENT_COLOR = "#687287";
    public static final String ANNOTATION_COLOR = "#FFC832";
    public static final String PACKAGE_COLOR = "#FFC832";
    public static final String PARAMETER_COLOR = "#FF7632";
    public static final String IDENTIFIER_COLOR = "#32F4FF";
    public static final String OPERATOR_COLOR = "#999999";
    public static final String ERROR_COLOR = "#FF3251";
    public static final String DEFAULT_COLOR = "#bbbbbb";

    private final ComponentBuilder builder = new ComponentBuilder();

    @Override
    public IFormatterContext keyword(String text) {
        this.builder.colored(text, KEYWORD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(String text) {
        this.builder.colored(text, NUMBER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(Number number) {
        this.number(String.valueOf(number));
        return this;
    }

    @Override
    public IFormatterContext string(String text) {
        this.builder.colored(text, STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext stringEscape(String text) {
        this.builder.colored(text, STRING_ESCAPE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext operator(String text) {
        this.builder.colored(text, OPERATOR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext identifier(String text) {
        this.builder.colored(text, IDENTIFIER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text) {
        this.builder.colored(text, PARAMETER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text, Object value) {
        this.parameter(text);
        this.operator(" = ");
        this.other(value);
        return this;
    }

    @Override
    public IFormatterContext comment(String text) {
        this.builder.colored(text, COMMENT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext error(String text) {
        this.builder.colored(text, ERROR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext className(String text) {
        this.builder.colored(text, CLASS_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(String text) {
        this.builder.colored(text, ENUM_CONST_COLOR);
        return this;
    }

    @Override
    public IFormatterContext packageName(String text) {
        this.builder.colored(text, PACKAGE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext methodName(String text) {
        this.builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext functionName(String text) {
        this.builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext callName(String text) {
        this.builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext field(String text) {
        this.builder.colored(text, FIELD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext annotation(String text) {
        this.builder.colored(text, ANNOTATION_COLOR);
        return this;
    }

    @Override
    public IFormatterContext normal(String text) {
        this.builder.colored(text, DEFAULT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(Enum<?> enumValue) {
        if (IAdvancedDebug.get().isEnumConstantsSpaced()) {
            this.enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", " "));
        } else {
            this.enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", "-"));
        }
        return this;
    }

    @Override
    public IFormatterContext classValue(Class<?> clazz) {
        this.packageName(clazz.getPackage().getName() + ".");
        this.className(clazz.getName());
        return this;
    }

    @Override
    public IFormatterContext space() {
        this.normal(" ");
        return this;
    }

    @Override
    public IFormatterContext separator() {
        this.operator(", ");
        return this;
    }

    @Override
    public IFormatterContext hex(String hexString) {
        this.number(hexString);
        return this;
    }

    @Override
    public IFormatterContext hexValue(int number) {
        this.hex(Integer.toHexString(number));
        return this;
    }

    @Override
    public IFormatterContext intValue(int number) {
        this.number(Integer.toString(number));
        return this;
    }

    @Override
    public IFormatterContext longValue(long number) {
        this.number(Long.toString(number));
        return this;
    }

    @Override
    public IFormatterContext floatValue(float number) {
        this.number(Float.toString(number));
        return this;
    }

    @Override
    public IFormatterContext doubleValue(double number) {
        this.number(Double.toString(number));
        return this;
    }

    @Override
    public IFormatterContext other(Object obj) {
        FormatterContext ctx = new FormatterContext();
        DebugGui.get().format(obj, ctx);
        this.builder.append(ctx.build());
        return this;
    }

    @Override
    public IFormatterContext stringEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\"' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\"");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }

        this.builder.colored(current.toString(), STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext charsEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\'' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\'");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    this.builder.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }
        return this;
    }

    @Deprecated
    @Override
    public IFormatterContext direct(String alreadyFormatted) {
        this.builder.append(alreadyFormatted);
        return this;
    }

    @Override
    public void subFormat(Consumer<IFormatterContext> o) {
        FormatterContext ctx = new FormatterContext();
        o.accept(ctx);
        this.builder.append(ctx.build());
    }

    public Component build() {
        return this.builder.build();
    }
}
