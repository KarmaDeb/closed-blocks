package dev.tagless.craft.closedblocks.api.util.reflection;

import dev.tagless.craft.closedblocks.api.util.reflection.reflected.constructor.ReflectedConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * A configuration for {@link ReflectionUtils}. Allows to
 * cache different lookups
 */
public class ReflectiveConfiguration<T> {

    private final Class<T> type;
    private final Map<String, Constructor<T>> lookups = new LinkedHashMap<>();

    /**
     * Create a new reflective configuration
     * @param type the class type
     */
    public ReflectiveConfiguration(final @NotNull Class<T> type) {
        this.type = type;
    }

    /**
     * Create a new lookup
     * @param label the lookup label
     * @param types the lookup types
     */
    @SuppressWarnings("unchecked")
    public Optional<ReflectedConstructor<T>> lookup(final String label, final Class<?>... types) {
        if (this.lookups.containsKey(label)) {
            Constructor<T> constructor = this.lookups.get(label);
            return Optional.of(new ReflectedConstructor<>(label, constructor));
        }

        if (types == null || types.length == 0) {
            try {
                Constructor<T> constructor = this.type.getDeclaredConstructor();
                this.lookups.put(label, constructor);

                return Optional.of(new ReflectedConstructor<>(label, constructor));
            } catch (ReflectiveOperationException ex) {
                return Optional.empty();
            }
        }

        Constructor<T>[] constructors = (Constructor<T>[]) this.type.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length != types.length) {
                continue;
            }

            List<Class<?>> pT = discardClasses(types, params);
            if (pT.isEmpty()) {
                this.lookups.put(label, constructor);
                return Optional.of(new ReflectedConstructor<>(label, constructor));
            }
        }

        return Optional.empty();
    }

    private static @NotNull List<Class<?>> discardClasses(final @NotNull Class<?>[] paramTypes, final @NotNull Class<?>[] params) {
        List<Class<?>> pT = new ArrayList<>(Arrays.asList(paramTypes));
        for (Class<?> p : params) {
            Class<?> match = null;
            for (Class<?> ppT : pT) {
                if (p.equals(ppT)) {
                    match = ppT;
                    break;
                }
                if (p.isAssignableFrom(ppT)) {
                    match = ppT;
                }
            }

            if (match != null) {
                pT.remove(match);
            }
        }
        return pT;
    }
}
