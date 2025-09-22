package dev.tagless.craft.closedblocks.api.util.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Reflection helper class
 */
public final class ReflectionUtils {

    public ReflectionUtils() {}

    /**
     * Finds a constructor
     * @param label the constructor label
     * @param clazz the class in where to find the constructor
     * @param paramTypes the expected parameter types
     * @return the reflected constructor
     * @param <T> the instance type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> Optional<ReflectedConstructor<T>> findConstructor(final String label, final Class<T> clazz, final Class<?>... paramTypes) {
        if (paramTypes == null || paramTypes.length == 0) {
            try {
                return Optional.of(new ReflectedConstructor<>(label, clazz.getDeclaredConstructor()));
            } catch (ReflectiveOperationException ex) {
                return Optional.empty();
            }
        }

        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length != paramTypes.length) {
                continue;
            }

            List<Class<?>> pT = discardClasses(paramTypes, params);
            if (pT.isEmpty()) {
                return Optional.of(new ReflectedConstructor<>(label, constructor));
            }
        }

        return Optional.empty();
    }

    private static @NotNull List<Class<?>> discardClasses(final @NotNull Class<?>[] paramTypes, final @NotNull Class<?>[] params) {
        List<Class<?>> pT = Arrays.asList(paramTypes);
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
