package com.ultreon.mods.advanceddebug.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ExceptionList {
    private static final Deque<Throwable> EXCEPTION_STACK = new ArrayDeque<>();
    private static final Object lock = new Object();

    public static void push(Throwable e) {
        synchronized (lock) {
            EXCEPTION_STACK.addFirst(e);
            if (EXCEPTION_STACK.size() > 5) {
                EXCEPTION_STACK.removeLast();
            }
        }
    }

    public static List<Throwable> all() {
        return EXCEPTION_STACK.stream().toList();
    }
}
