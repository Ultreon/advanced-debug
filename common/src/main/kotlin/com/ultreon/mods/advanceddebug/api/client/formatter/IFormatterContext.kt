package com.ultreon.mods.advanceddebug.api.client.formatter

import com.ultreon.mods.advanceddebug.client.formatter.FormatterContext
import java.util.function.Consumer

interface IFormatterContext {
    /**
     * Format a keyword, and append it to the formatter.
     *
     * @param text the keyword to format
     * @return this formatter context
     */
    fun keyword(text: String): FormatterContext

    /**
     * Format a number, and append it to the formatter.
     *
     * @param text the number to format
     * @return this formatter context
     */
    fun number(text: String): FormatterContext

    /**
     * Format a number, and append it to the formatter.
     *
     * @param number the number to format
     * @return this formatter context
     */
    fun number(number: Number): FormatterContext

    /**
     * Format a string, and append it to the formatter.
     *
     * @param text the string to format
     * @return this formatter context
     */
    fun string(text: String): FormatterContext

    /**
     * Format a string escape, and append it to the formatter.
     *
     * @param text the string escape to format
     * @return this formatter context
     */
    fun stringEscape(text: String): FormatterContext

    /**
     * Format an operator, and append it to the formatter.
     *
     * @param text the operator to format
     * @return this formatter context
     */
    fun operator(text: String): FormatterContext

    /**
     * Format an identifier, and append it to the formatter.
     *
     * @param text the identifier to format
     * @return this formatter context
     */
    fun identifier(text: String): FormatterContext

    /**
     * Format a parameter, and append it to the formatter.
     *
     * @param text the parameter to format
     * @return this formatter context
     */
    fun parameter(text: String): FormatterContext

    /**
     * Format a parameter with a value, and append it to the formatter.
     *
     * @param text  the parameter to format
     * @param value the value to format
     * @return this formatter context
     */
    fun parameter(text: String, value: Any?): FormatterContext

    /**
     * Format a comment, and append it to the formatter.
     *
     * @param text the comment to format
     * @return this formatter context
     */
    fun comment(text: String): FormatterContext

    /**
     * Format an error, and append it to the formatter.
     *
     * @param text the error to format
     * @return this formatter context
     */
    fun error(text: String): FormatterContext

    /**
     * Format a class name, and append it to the formatter.
     *
     * @param text the class name to format
     * @return this formatter context
     */
    fun className(text: String): FormatterContext

    /**
     * Format an enum constant, and append it to the formatter.
     *
     * @param enumValue the enum constant to format
     * @return this formatter context
     */
    fun enumConstant(enumValue: Enum<*>): FormatterContext

    /**
     * Format an enum constant, and append it to the formatter.
     *
     * @param text the enum constant to format
     * @return this formatter context
     */
    fun enumConstant(text: String): FormatterContext

    /**
     * Format a package name, and append it to the formatter.
     *
     * @param text the package name to format
     * @return this formatter context
     */
    fun packageName(text: String): FormatterContext

    /**
     * Format a method name, and append it to the formatter.
     *
     * @param text the method name to format
     * @return this formatter context
     */
    fun methodName(text: String): FormatterContext

    /**
     * Format a function name, and append it to the formatter.
     *
     * @param text the function name to format
     * @return this formatter context
     */
    fun functionName(text: String): FormatterContext

    /**
     * Format a call to a method, and append it to the formatter.
     *
     * @param text the call to a method to format
     * @return this formatter context
     */
    fun callName(text: String): FormatterContext

    /**
     * Format a field name, and append it to the formatter.
     *
     * @param text the field name to format
     * @return this formatter context
     */
    fun field(text: String): FormatterContext

    /**
     * Format an annotation, and append it to the formatter.
     *
     * @param text the annotation to format
     * @return this formatter context
     */
    fun annotation(text: String): FormatterContext

    /**
     * Format normal text, and append it to the formatter.
     *
     * @param text the text to format
     * @return this formatter context
     */
    fun normal(text: String): FormatterContext

    /**
     * Format a class, and append it to the formatter.
     *
     * @param clazz the class to format
     * @return this formatter context
     */
    fun classValue(clazz: Class<*>): FormatterContext

    /**
     * Append a space to the formatter.
     *
     * @return this formatter context
     */
    fun space(): FormatterContext

    /**
     * Append a separator to the formatter.
     *
     * @return this formatter context
     */
    fun separator(): FormatterContext

    /**
     * Format a hexadecimal number, and append it to the formatter.
     *
     * @param hexString the hexadecimal number to format
     * @return this formatter context
     */
    fun hex(hexString: String): FormatterContext

    /**
     * Format a hexadecimal number, and append it to the formatter.
     *
     * @param number the hexadecimal number to format
     * @return this formatter context
     */
    fun hexValue(number: Int): FormatterContext

    /**
     * Format an integer, and append it to the formatter.
     *
     * @param number the integer to format
     * @return this formatter context
     */
    fun intValue(number: Int): FormatterContext

    /**
     * Format a long, and append it to the formatter.
     *
     * @param number the long to format
     * @return this formatter context
     */
    fun longValue(number: Long): FormatterContext

    /**
     * Format a float, and append it to the formatter.
     *
     * @param number the float to format
     * @return this formatter context
     */
    fun floatValue(number: Float): FormatterContext

    /**
     * Format a double, and append it to the formatter.
     *
     * @param number the double to format
     * @return this formatter context
     */
    fun doubleValue(number: Double): FormatterContext

    /**
     * Format a string with escape characters, and append it to the formatter.
     *
     * @param text the string to format with escape characters
     * @return this formatter context
     */
    fun stringEscaped(text: String): FormatterContext

    /**
     * Format a character with escape characters, and append it to the formatter.
     *
     * @param text the character to format with escape characters
     * @return this formatter context
     */
    fun charsEscaped(text: String): FormatterContext

    /**
     * Format another object, and append it to the formatter.
     *
     * @param obj the object to format
     * @return this formatter
     */
    fun other(obj: Any?): FormatterContext

    /**
     * Directly append the given text to the output.
     *
     * @param alreadyFormatted the already formatted string.
     * @return this formatter
     */
    @Deprecated("Don't use this method, this is for internal use only.")
    fun direct(alreadyFormatted: String): FormatterContext
    fun subFormat(o: Consumer<IFormatterContext>)
}