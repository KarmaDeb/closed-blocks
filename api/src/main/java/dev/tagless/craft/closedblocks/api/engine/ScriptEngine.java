package dev.tagless.craft.closedblocks.api.engine;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * The script engine allows to run scripting files which
 * directly interact with the plugin API. The scripting engine
 * exists mainly for tasks such as recipe registration, as it
 * allows to generate dynamic and more complex recipes without
 * needing a "complex" configuration syntax
 */
public abstract class ScriptEngine {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final Collection<Javatype> allowedTypes = ConcurrentHashMap.newKeySet();
    private final Map<String, Object> bindings = new LinkedHashMap<>();

    /**
     * Creates a new script engine
     * @param types the allowed types
     */
    public ScriptEngine(final Javatype... types) {
        this.allowedTypes.add(Javatype.pack("org.bukkit."));
        this.allowedTypes.addAll(Arrays.asList(types));
    }

    /**
     * Get if an accessor is allowed by this
     * script engine
     * @param accessor the accessor
     * @return if the accessor is allowed
     */
    public final boolean isAllowed(final String accessor) {
        return this.allowedTypes.stream().anyMatch(p -> p.test(accessor));
    }

    /**
     * Get if an accessor is allowed by this
     * script engine
     * @param accessor the accessor
     * @return if the accessor is allowed
     */
    public final boolean notAllowed(final String accessor) {
        return this.allowedTypes.stream().noneMatch(p -> p.test(accessor));
    }

    /**
     * Adds a binding to the script engine
     * global scope. Every object will be available
     * to every script which runs by this engine
     * @param name the variable name
     * @param value the variable value
     * @return the script engine instance
     */
    public final ScriptEngine bind(final String name, final Object value) {
        if (value == null) {
            this.bindings.put(name, null);
            return this;
        }

        Class<?> bindingClass = value.getClass();

        String className = bindingClass.getCanonicalName();
        if (this.notAllowed(className)) {
            Javatype type = Javatype.type(bindingClass);
            this.allowedTypes.add(type);
        }

        this.bindings.put(name, value);
        return this;
    }

    /**
     * Executes a script file on the current thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @Contract("null, null -> fail")
    public final void execute(final Path file, final Consumer<Map<String, Object>> bindMapper) {
        try (InputStream stream = Files.newInputStream(file)) {
            this.execute(stream, bindMapper);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes a script file without blocking the main thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @NotNull @Contract("null, null -> fail")
    public final CompletableFuture<Void> executeAsync(final Path file,
                                                final Consumer<Map<String, Object>> bindMapper) {
        return CompletableFuture.runAsync(() -> this.execute(file, bindMapper), EXECUTOR);
    }

    /**
     * Executes a script file on the main thread
     * @param file the script file to run
     */
    @Contract("null -> fail")
    public final void execute(final Path file) {
        this.execute(file, ignored -> {});
    }

    /**
     * Executes a script file without blocking the main thread
     * @param file the script file to run
     */
    @NotNull @Contract("null -> fail")
    public final CompletableFuture<Void> executeAsync(final Path file) {
        return this.executeAsync(file, ignored -> {});
    }

    /**
     * Executes a script file on the current thread
     * @param stream the script stream to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @Contract("null, null -> fail")
    public final void execute(final InputStream stream, final Consumer<Map<String, Object>> bindMapper) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder scriptBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptBuilder.append(line)
                        .append("\n");
            }

            this.execute(scriptBuilder, bindMapper);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes a script stream without blocking the main thread
     * @param stream the script stream to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @NotNull @Contract("null, null -> fail")
    public final CompletableFuture<Void> executeAsync(final InputStream stream,
                                                final Consumer<Map<String, Object>> bindMapper) {
        return CompletableFuture.runAsync(() -> this.execute(stream, bindMapper), EXECUTOR);
    }

    /**
     * Executes a script stream on the main thread
     * @param stream the script stream to run
     */
    @Contract("null -> fail")
    public final void execute(final InputStream stream) {
        this.execute(stream, ignored -> {});
    }

    /**
     * Executes a script stream without blocking the main thread
     * @param stream the script stream to run
     */
    @NotNull @Contract("null -> fail")
    public final CompletableFuture<Void> executeAsync(final InputStream stream) {
        return this.executeAsync(stream, ignored -> {});
    }

    /**
     * Executes a script on the current thread
     * @param script the script to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @Contract("null, null -> fail")
    public final void execute(final CharSequence script, final Consumer<Map<String, Object>> bindMapper) {
        Collection<Javatype> allowedTypes = new HashSet<>(this.allowedTypes);
        Map<String, Object> binds = this.mapBindings(
                Objects.requireNonNull(bindMapper, "bindMapper cannot be null"), allowedTypes);
        binds.putAll(this.bindings);

        this.executeScript(Objects.requireNonNull(script, "script cannot be null"), allowedTypes, binds);
    }

    /**
     * Executes a script without blocking the main thread
     * @param script the script to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @NotNull @Contract("null, null -> fail")
    public final CompletableFuture<Void> executeAsync(final CharSequence script,
                                                final Consumer<Map<String, Object>> bindMapper) {
        return CompletableFuture.runAsync(() -> this.execute(script, bindMapper), EXECUTOR);
    }

    /**
     * Executes a script on the main thread
     * @param script the script to run
     */
    @Contract("null -> fail")
    public final void execute(final CharSequence script) {
        this.execute(script, ignored -> {});
    }

    /**
     * Executes a script without blocking the main thread
     * @param script the script to run
     */
    @NotNull @Contract("null -> fail")
    public final CompletableFuture<Void> executeAsync(final CharSequence script) {
        return this.executeAsync(script, ignored -> {});
    }

    /**
     * Executes the script
     * @param script the script
     * @param javatypes the javatypes
     * @param mappedBindings the mapped bindings
     */
    protected abstract void executeScript(final @NotNull CharSequence script,
                                 final @NotNull Collection<Javatype> javatypes,
                                 final @NotNull Map<String, Object> mappedBindings);

    private Map<String, Object> mapBindings(final Consumer<Map<String, Object>> bindMapper,
                                           final Collection<Javatype> allowedClasses) {
        Map<String, Object> mappedBindings = new HashMap<>();

        if (bindMapper != null) {
            bindMapper.accept(mappedBindings);

            for (Map.Entry<String, Object> entry : mappedBindings.entrySet()) {
                Object value = entry.getValue();
                Class<?> vc = value.getClass();
                String className = vc.getCanonicalName();

                if (this.notAllowed(className)) {
                    Javatype type = Javatype.type(vc);
                    allowedClasses.add(type);
                }
            }
        }

        return mappedBindings;
    }
}