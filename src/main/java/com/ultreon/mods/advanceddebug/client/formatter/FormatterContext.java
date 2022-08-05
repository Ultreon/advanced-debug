package com.ultreon.mods.advanceddebug.client.formatter;

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.client.menu.DebugGui;
import com.ultreon.mods.advanceddebug.text.ComponentBuilder;
import net.minecraft.network.chat.Component;

import java.util.Locale;

@SuppressWarnings("SpellCheckingInspection")
public class FormatterContext implements IFormatterContext {
    public static final String KEYWORD_COLOR = "#EA32FF";
    public static final String NUMBER_COLOR = "#FF9232";
    public static final String ENUM_CONST = "#FF9232";
    public static final String STRING_COLOR = "#5FFF32";
    public static final String STRING_ESCAPE_COLOR = "#32F4FF";
    public static final String METHOD_COLOR = "#32A3FF";
    public static final String FIELD_COLOR = "#FF3251";
    public static final String CLASS_COLOR = "#FFC832";
    public static final String COMMENT_COLOR = "#687287";
    public static final String ANNOTATION_COLOR = "#FFC832";
    public static final String PACKAGE_COLOR = "#FFC832";
    public static final String PARAMETER_COLOR = "#FF9232";
    public static final String IDENTIFIER_COLOR = "#32F4FF";
    public static final String OPERATOR_COLOR = "#999999";
    public static final String ERROR_COLOR = "#FF3251";
    public static final String DEFAULT_COLOR = "#bbbbbb";

    private final ComponentBuilder builder = new ComponentBuilder();

    @Override
    public IFormatterContext keyword(String text) {
        builder.colored(text, KEYWORD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(String text) {
        builder.colored(text, NUMBER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(Number number) {
        number(String.valueOf(number));
        return this;
    }

    @Override
    public IFormatterContext string(String text) {
        builder.colored(text, STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext stringEscape(String text) {
        builder.colored(text, STRING_ESCAPE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext operator(String text) {
        builder.colored(text, OPERATOR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext identifier(String text) {
        builder.colored(text, IDENTIFIER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text) {
        builder.colored(text, PARAMETER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text, Object value) {
        parameter(text);
        operator(" = ");
        other(value);
        return this;
    }

    @Override
    public IFormatterContext comment(String text) {
        builder.colored(text, COMMENT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext error(String text) {
        builder.colored(text, ERROR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext className(String text) {
        builder.colored(text, CLASS_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(String text) {
        builder.colored(text, ENUM_CONST);
        return this;
    }

    @Override
    public IFormatterContext packageName(String text) {
        builder.colored(text, PACKAGE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext methodName(String text) {
        builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext functionName(String text) {
        builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext callName(String text) {
        builder.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext field(String text) {
        builder.colored(text, FIELD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext annotation(String text) {
        builder.colored(text, ANNOTATION_COLOR);
        return this;
    }

    @Override
    public IFormatterContext normal(String text) {
        builder.colored(text, DEFAULT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(Enum<?> enumValue) {
        enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", " "));
        return this;
    }

    @Override
    public IFormatterContext classValue(Class<?> clazz) {
        packageName(clazz.getPackage().getName() + ".");
        className(clazz.getName());
        return this;
    }

    @Override
    public IFormatterContext space() {
        normal(" ");
        return this;
    }

    @Override
    public IFormatterContext separator() {
        operator(", ");
        return this;
    }

    @Override
    public IFormatterContext hex(String hexString) {
        number(hexString);
        return this;
    }

    @Override
    public IFormatterContext hexValue(int number) {
        hex(Integer.toHexString(number));
        return this;
    }

    @Override
    public IFormatterContext intValue(int number) {
        number(Integer.toString(number));
        return this;
    }

    @Override
    public IFormatterContext longValue(long number) {
        number(Long.toString(number));
        return this;
    }

    @Override
    public IFormatterContext floatValue(float number) {
        number(Float.toString(number));
        return this;
    }

    @Override
    public IFormatterContext doubleValue(double number) {
        number(Double.toString(number));
        return this;
    }

    @Override
    public IFormatterContext other(Object o) {
        FormatterContext ctx = new FormatterContext();
        DebugGui.get().format(o, ctx);
        builder.append(ctx.build());
        return this;
    }

    @Override
    public IFormatterContext stringEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\"' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\"");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }
        return this;
    }

    @Override
    public IFormatterContext charsEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\'' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\'");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    builder.colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }
        return this;
    }

    public Component build() {
        return builder.build();
    }
}
