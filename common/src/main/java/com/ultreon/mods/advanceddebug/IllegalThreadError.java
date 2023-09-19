package com.ultreon.mods.advanceddebug;

public class IllegalThreadError extends Error {
    public IllegalThreadError() {
        super();
    }

    public IllegalThreadError(String message) {
        super(message);
    }

    public IllegalThreadError(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalThreadError(Throwable cause) {
        super(cause);
    }
}
