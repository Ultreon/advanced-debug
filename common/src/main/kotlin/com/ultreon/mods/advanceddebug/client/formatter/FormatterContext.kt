package com.ultreon.mods.advanceddebug.client.formatter

import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext
import com.ultreon.mods.advanceddebug.client.Config
import com.ultreon.mods.advanceddebug.client.menu.DebugGui
import com.ultreon.mods.advanceddebug.text.ComponentBuilder
import net.minecraft.network.chat.Component
import java.util.function.Consumer

class FormatterContext : IFormatterContext {
    private val builder = ComponentBuilder()
    override fun keyword(text: String): FormatterContext {
        builder.colored(text, KEYWORD_COLOR)
        return this
    }

    override fun number(text: String): FormatterContext {
        builder.colored(text, NUMBER_COLOR)
        return this
    }

    override fun number(number: Number): FormatterContext {
        number(number.toString())
        return this
    }

    override fun string(text: String): FormatterContext {
        builder.colored(text, STRING_COLOR)
        return this
    }

    override fun stringEscape(text: String): FormatterContext {
        builder.colored(text, STRING_ESCAPE_COLOR)
        return this
    }

    override fun operator(text: String): FormatterContext {
        builder.colored(text, OPERATOR_COLOR)
        return this
    }

    override fun identifier(text: String): FormatterContext {
        builder.colored(text, IDENTIFIER_COLOR)
        return this
    }

    override fun parameter(text: String): FormatterContext {
        builder.colored(text, PARAMETER_COLOR)
        return this
    }

    override fun parameter(text: String, value: Any?): FormatterContext {
        parameter(text)
        operator(" = ")
        other(value)
        return this
    }

    override fun comment(text: String): FormatterContext {
        builder.colored(text, COMMENT_COLOR)
        return this
    }

    override fun error(text: String): FormatterContext {
        builder.colored(text, ERROR_COLOR)
        return this
    }

    override fun className(text: String): FormatterContext {
        builder.colored(text, CLASS_COLOR)
        return this
    }

    override fun enumConstant(text: String): FormatterContext {
        builder.colored(text, ENUM_CONST_COLOR)
        return this
    }

    override fun packageName(text: String): FormatterContext {
        builder.colored(text, PACKAGE_COLOR)
        return this
    }

    override fun methodName(text: String): FormatterContext {
        builder.colored(text, METHOD_COLOR)
        return this
    }

    override fun functionName(text: String): FormatterContext {
        builder.colored(text, METHOD_COLOR)
        return this
    }

    override fun callName(text: String): FormatterContext {
        builder.colored(text, METHOD_COLOR)
        return this
    }

    override fun field(text: String): FormatterContext {
        builder.colored(text, FIELD_COLOR)
        return this
    }

    override fun annotation(text: String): FormatterContext {
        builder.colored(text, ANNOTATION_COLOR)
        return this
    }

    override fun normal(text: String): FormatterContext {
        builder.colored(text, DEFAULT_COLOR)
        return this
    }

    override fun enumConstant(enumValue: Enum<*>): FormatterContext {
        if (Config.spacedEnumConstants.get()) {
            enumConstant(enumValue.name.lowercase().replace("_".toRegex(), " "))
        } else {
            enumConstant(enumValue.name.lowercase().replace("_".toRegex(), "-"))
        }
        return this
    }

    override fun classValue(clazz: Class<*>): FormatterContext {
        packageName(clazz.getPackage().name + ".")
        className(clazz.name)
        return this
    }

    override fun space(): FormatterContext {
        normal(" ")
        return this
    }

    override fun separator(): FormatterContext {
        operator(", ")
        return this
    }

    override fun hex(hexString: String): FormatterContext {
        number(hexString)
        return this
    }

    override fun hexValue(number: Int): FormatterContext {
        hex(Integer.toHexString(number))
        return this
    }

    override fun intValue(number: Int): FormatterContext {
        number(number.toString())
        return this
    }

    override fun longValue(number: Long): FormatterContext {
        number(number.toString())
        return this
    }

    override fun floatValue(number: Float): FormatterContext {
        number(number.toString())
        return this
    }

    override fun doubleValue(number: Double): FormatterContext {
        number(number.toString())
        return this
    }

    override fun other(obj: Any?): FormatterContext {
        val ctx = FormatterContext()
        DebugGui.get().format(obj, ctx)
        builder.append(ctx.build())
        return this
    }

    override fun stringEscaped(text: String): FormatterContext {
        var current = StringBuilder()
        for (c in text.toCharArray()) {
            when (c) {
                '\b' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\b")
                    current = StringBuilder()
                }

                '\t' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\t")
                    current = StringBuilder()
                }

                '\n' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\n")
                    current = StringBuilder()
                }

                '\u000c' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\u000c")
                    current = StringBuilder()
                }

                '\r' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\r")
                    current = StringBuilder()
                }

                '\"' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\\"")
                    current = StringBuilder()
                }

                '\\' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\\\")
                    current = StringBuilder()
                }

                else -> current.append(c)
            }
        }
        builder.colored(current.toString(), STRING_COLOR)
        return this
    }

    override fun charsEscaped(text: String): FormatterContext {
        var current = StringBuilder()
        for (c in text.toCharArray()) {
            when (c) {
                '\b' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\b")
                    current = StringBuilder()
                }

                '\t' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\t")
                    current = StringBuilder()
                }

                '\n' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\n")
                    current = StringBuilder()
                }

                '\u000c' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\u000c")
                    current = StringBuilder()
                }

                '\r' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\r")
                    current = StringBuilder()
                }

                '\'' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\'")
                    current = StringBuilder()
                }

                '\\' -> {
                    builder.colored(current.toString(), STRING_COLOR)
                    stringEscape("\\\\")
                    current = StringBuilder()
                }

                else -> current.append(c)
            }
        }
        return this
    }

    @Deprecated("")
    override fun direct(alreadyFormatted: String): FormatterContext {
        builder.append(alreadyFormatted)
        return this
    }

    override fun subFormat(o: Consumer<IFormatterContext>) {
        val ctx = FormatterContext()
        o.accept(ctx)
        builder.append(ctx.build())
    }

    fun build(): Component {
        return builder.build()
    }

    companion object {
        const val KEYWORD_COLOR = "#EA32FF"
        const val NUMBER_COLOR = "#FF7632"
        const val ENUM_CONST_COLOR = "#FF7632"
        const val STRING_COLOR = "#5FFF32"
        const val STRING_ESCAPE_COLOR = "#32F4FF"
        const val METHOD_COLOR = "#32A3FF"
        const val FIELD_COLOR = "#FF3251"
        const val CLASS_COLOR = "#FFC832"
        const val COMMENT_COLOR = "#687287"
        const val ANNOTATION_COLOR = "#FFC832"
        const val PACKAGE_COLOR = "#FFC832"
        const val PARAMETER_COLOR = "#FF7632"
        const val IDENTIFIER_COLOR = "#32F4FF"
        const val OPERATOR_COLOR = "#999999"
        const val ERROR_COLOR = "#FF3251"
        const val DEFAULT_COLOR = "#bbbbbb"
    }
}