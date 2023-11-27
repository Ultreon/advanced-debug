package com.ultreon.mods.advanceddebug.inspect;

import com.ultreon.mods.advanceddebug.util.McDisposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The root node of the inspection tree.
 * Inspection trees are a way of determining values within the game.
 * It also allows for inspecting game packets.
 *
 * @author <a href="https://github.com/XyperCode">XyperCode</a>
 * @since 0.1.0
 */
public final class InspectionRoot<T> extends InspectionNode<T> implements McDisposable {
    private boolean inspecting;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Map<Class<?>, Consumer<InspectionNode<?>>> AUTO_FILL = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Function<?, String>> FORMATTERS = new ConcurrentHashMap<>();

    /**
     * Constructs a new root inspection node.
     *
     * @param rootValue the root node value.
     */
    public InspectionRoot(T rootValue) {
        super("", null, null, n -> rootValue);
    }

    /**
     * Format a value using the formatters registered with {@link #registerFormatter(Class, Function)}.
     *
     * @param o the value to format.
     * @return the formatted value.
     */
    @SuppressWarnings("unchecked")
    public static String format(@Nullable Object o) {
        if (o == null) {
            return "null";
        }

        var formatter = (Function<Object, String>) InspectionRoot.FORMATTERS.get(o.getClass());
        if (formatter != null) {
            try {
                return formatter.apply(o);
            } catch (Throwable ignored) {

            }
        }
        try {
            return String.valueOf(o);
        } catch (Throwable t) {
            return o.getClass().getName();
        }
    }

    public boolean isInspecting() {
        return this.inspecting;
    }

    public void setInspecting(boolean inspecting) {
        this.inspecting = inspecting;
    }

    @Override
    public @NotNull InspectionRoot<T> getRoot() {
        return this;
    }

    static <N> void fill(InspectionNode<N> node, Class<?> clazz) {
        if (clazz.isArray()) {
            var componentType = clazz.getComponentType();
            try {
                var nodeValue = node.getValue();
                var length = Array.getLength(nodeValue);
                for (var i = 0; i < length; i++) {
                    try {
                        var value = Array.get(nodeValue, i);
                        InspectionNode<Object> created;
                        if (value != null && value.getClass().isArray()) {
                            created = node.createNode(i + " (Array)", () -> value);
                        } else if (value != null) {
                            created = node.createNode(i + " (" + format(value) + ")", () -> value);
                        } else {
                            created = node.createNode(i + " (null)", () -> "null");
                        }
                        InspectionRoot.fill(created, componentType);
                    } catch (Throwable e) {
                        node.create("failure", e::getMessage);
                    }
                }
            } catch (Throwable e) {
                node.create("failure", e::getMessage);
            }
        }

        var filler = InspectionRoot.AUTO_FILL.get(clazz);
        if (filler != null) {
            filler.accept(node);
        }

        while (clazz != null) {
            filler = InspectionRoot.AUTO_FILL.get(clazz);
            if (filler != null) {
                filler.accept(node);
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Register an object formatter for a given class.
     *
     * @param clazz the class to format.
     * @param formatter the formatter to register.
     * @param <E> the type of the object to format.
     */
    public static <E> void registerFormatter(Class<E> clazz, Function<E, String> formatter) {
        InspectionRoot.FORMATTERS.put(clazz, formatter);
    }

    /**
     * Register an auto-filler for a given class.
     *
     * @param clazz the class to autofill.
     * @param filler the auto-filler to register
     * @param <N> the type of the node.
     */
    @SuppressWarnings("unchecked")
    public static <N> void registerAutoFill(Class<N> clazz, Consumer<@NotNull InspectionNode<N>> filler) {
        InspectionRoot.AUTO_FILL.put(clazz, inspectionNode -> filler.accept((InspectionNode<N>) inspectionNode));
    }

    /**
     * Get the node at the given path.
     *
     * @param path the path of the node.
     * @return the node at the given path, or {@code null} if the path is invalid.
     */
    public @Nullable InspectionNode<?> getNode(String path) {
        if (path.equals("/")) return this;
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (!path.matches("(/[^/]+)+")) throw new IllegalArgumentException("Invalid path: " + path);

        var pathParts = path.substring(1).split("/");
        InspectionNode<?> node = null;
        for (var name : pathParts) {
            node = Objects.requireNonNullElse(node, this).getNodes().get(name);

            if (node == null) return null;
        }

        if (node == null) throw new InternalError("Profile section not found: " + path);

        return node;
    }

    /**
     * Dispose the inspection tree.
     */
    public void dispose() {
        this.scheduler.shutdownNow();
    }
}
