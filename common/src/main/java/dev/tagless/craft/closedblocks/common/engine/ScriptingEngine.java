package dev.tagless.craft.closedblocks.common.engine;

import dev.tagless.craft.closedblocks.api.engine.ScriptEngine;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ScriptingEngine implements ScriptEngine {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String LANGUAGE_JAVASCRIPT = "js";
    private static final String[] SUPPORTED_LANGUAGES = {
            LANGUAGE_JAVASCRIPT
    };

    private static final Map<Class<?>, Collection<String>> discovered = new ConcurrentHashMap<>();

    private final Set<String> allowedPackages = new LinkedHashSet<>();
    private final Map<String, Object> bindings = new LinkedHashMap<>();

    public ScriptingEngine(final String... allowedPackages) {
        this.allowedPackages.add("org.bukkit.");
        this.allowedPackages.addAll(Arrays.asList(allowedPackages));
    }

    /**
     * Adds a binding to the script engine
     * global scope. Every object will be available
     * to every script which runs by this engine
     * @param name the variable name
     * @param value the variable value
     * @return the script engine instance
     */
    @Override
    public ScriptingEngine bind(final String name, final Object value) {
        if (value == null) {
            bindings.put(name, null);
            return this;
        }

        Class<?> bindingClass = value.getClass();

        String className = bindingClass.getCanonicalName();
        if (this.allowedPackages.stream().noneMatch(className::startsWith)) {
            this.mapAllowedPackagesFromJavaClass(bindingClass, this.allowedPackages, new HashSet<>());
        }

        this.bindings.put(name, value);
        return this;
    }

    private void mapAllowedPackagesFromJavaClass(final Class<?> clazz,
                                                 final Collection<String> out,
                                                 final Collection<Class<?>> visited) {
        if (discovered.containsKey(clazz)) {
            out.addAll(discovered.get(clazz));
            visited.add(clazz);

            return;
        }

        if (visited.add(clazz)) {
            Collection<String> classes = new HashSet<>();

            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    continue;
                }

                Class<?> returnType = method.getReturnType();
                if (returnType.equals(void.class)) {
                    continue;
                }

                String returnTypeClassName = returnType.getCanonicalName();
                if (out.stream().noneMatch(p -> p.startsWith(returnTypeClassName))) {
                    classes.add(returnTypeClassName);
                }

                this.mapAllowedPackagesFromJavaClass(returnType, out, visited);
            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    continue;
                }

                Class<?> returnType = field.getType();
                String returnTypeClassName = returnType.getCanonicalName();
                if (out.stream().noneMatch(p -> p.startsWith(returnTypeClassName))) {
                    classes.add(returnTypeClassName);
                }

                this.mapAllowedPackagesFromJavaClass(returnType, out, visited);
            }

            out.addAll(classes);
            if (!out.equals(this.allowedPackages)) {
                discovered.put(clazz, classes);
            }
        }
    }

    /**
     * Executes a script file on the current thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @Override
    public void execute(final @NotNull Path file, final @NotNull Consumer<Map<String, Object>> bindMapper) {
        this.runScriptFile(file, bindMapper);
    }

    /**
     * Executes a script file without blocking the main thread
     * @param file the script file to run
     * @param bindMapper the "context" bindings, similar to {@link #bind(String, Object)} but
     *                   whose bindings will exist only for this script execution
     */
    @Override
    public CompletableFuture<Void> executeAsync(final @NotNull Path file,
                                                final @NotNull Consumer<Map<String, Object>> bindMapper) {
        return CompletableFuture.runAsync(() -> this.execute(file, bindMapper), EXECUTOR);
    }

    private void runScriptFile(final Path file,
                                                  final Consumer<Map<String, Object>> bindMapper) {
        Collection<String> allowedClasses = new HashSet<>(this.allowedPackages);
        Map<String, Object> binds = this.mapBindings(bindMapper, allowedClasses);

        try(Context context = Context.newBuilder(SUPPORTED_LANGUAGES)
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup(c -> allowedClasses.stream().anyMatch(c::startsWith))
            .allowIO(IOAccess.NONE)
            .allowCreateProcess(false)
            .allowCreateThread(false)
            .allowEnvironmentAccess(EnvironmentAccess.NONE)
            .allowNativeAccess(false)
            .build()) {

            File f = file.toFile();
            String languageId = Source.findLanguage(f);
            Source source = Source.newBuilder(languageId, f)
                .cached(true)
                .interactive(false)
                .internal(false)
                .build();

            Value bindings = context.getBindings(languageId);
            if (bindings != null) {
                binds.forEach(bindings::putMember);
                this.bindings.forEach(bindings::putMember);
            }

            context.eval(source);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<String, Object> mapBindings(final Consumer<Map<String, Object>> bindMapper,
                                           final Collection<String> allowedClasses) {
        Map<String, Object> mappedBindings = new HashMap<>();

        if (bindMapper != null) {
            bindMapper.accept(mappedBindings);


            for (Map.Entry<String, Object> entry : mappedBindings.entrySet()) {
                Object value = entry.getValue();
                Class<?> vc = value.getClass();
                String className = vc.getCanonicalName();

                if (this.allowedPackages.stream().noneMatch(className::startsWith)) {
                    this.mapAllowedPackagesFromJavaClass(vc, allowedClasses, new HashSet<>());
                    allowedClasses.add(className);
                }
            }
        }

        return mappedBindings;
    }
}