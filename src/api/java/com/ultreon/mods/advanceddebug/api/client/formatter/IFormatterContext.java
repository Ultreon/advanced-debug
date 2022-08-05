package com.ultreon.mods.advanceddebug.api.client.formatter;

@SuppressWarnings("UnusedReturnValue")
public interface IFormatterContext {
    IFormatterContext keyword(String text);

    IFormatterContext number(String text);

    IFormatterContext number(Number number);

    IFormatterContext string(String text);

    IFormatterContext stringEscape(String text);

    IFormatterContext operator(String text);

    IFormatterContext identifier(String text);

    IFormatterContext parameter(String text);

    IFormatterContext parameter(String text, Object value);

    IFormatterContext comment(String text);

    IFormatterContext error(String text);

    IFormatterContext className(String text);

    IFormatterContext enumConstant(Enum<?> enumValue);

    IFormatterContext enumConstant(String text);

    IFormatterContext packageName(String text);

    IFormatterContext methodName(String text);

    IFormatterContext functionName(String text);

    IFormatterContext callName(String text);

    IFormatterContext field(String text);

    IFormatterContext annotation(String text);

    IFormatterContext normal(String text);

    IFormatterContext classValue(Class<?> clazz);

    IFormatterContext space();

    IFormatterContext separator();

    IFormatterContext hex(String hexString);

    IFormatterContext hexValue(int number);

    IFormatterContext intValue(int number);

    IFormatterContext longValue(long number);

    IFormatterContext floatValue(float number);

    IFormatterContext doubleValue(double number);

    IFormatterContext stringEscaped(String text);

    IFormatterContext charsEscaped(String text);

    IFormatterContext other(Object e);
}
