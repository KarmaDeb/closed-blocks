package dev.tagless.craft.closedblocks.api.engine;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The script engine allows to run scripting files which
 * directly interact with the plugin API. The scripting engine
 * exists mainly for tasks such as recipe registration, as it
 * allows to generate dynamic and more complex recipes without
 * needing a "complex" configuration syntax
 */
public interface ScriptEngine {

    /**
     * Adds a binding to the script engine
     * global scope. Every object will be available
     * to every script which runs by this engine
     * @param name the variable name
     * @param value the variable value
     * @return the script engine instance
     */
    ScriptEngine bind(final String name, final Object value);

    /**
     * Executes a script file on the current thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    void execute(final @NotNull Path file, final @NotNull Consumer<Map<String, Object>> bindMapper);

    /**
     * Executes a script file without blocking the main thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    CompletableFuture<Void> executeAsync(final @NotNull Path file,
                                                final @NotNull Consumer<Map<String, Object>> bindMapper);

        /**
     * Executes a script file on the main thread
     * @param file the script file to run
     */
    default void execute(final @NotNull Path file) {
        this.execute(file, ignored -> {});
    }

    /**
     * Executes a script file without blocking the main thread
     * @param file the script file to run
     */
    default CompletableFuture<Void> executeAsync(final @NotNull Path file) {
        return this.executeAsync(file, ignored -> {});
    }
}
