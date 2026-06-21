package dev.tagless.craft.closedblocks.api.util.reflection.reflected.constructor;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * A reflected constructor
 */
@SuppressWarnings("unused")
public final class ReflectedConstructor<T> {

    private final String label;
    private final Constructor<T> constructor;

    /**
     * Create a new reflected constructor
     * @param label the constructor label
     * @param constructor the constructor
     */
    public ReflectedConstructor(final @NotNull String label, final @NotNull Constructor<T> constructor) {
        this.label = label;
        this.constructor = constructor;
    }

    /**
     * Get the label
     * @return the reflected constructor
     * label
     */
    @NotNull @Deprecated(forRemoval = true)
    public String getLabel() {
        return this.label;
    }

    /**
     * Get the constructor length
     * @return the amount of parameters
     */
    public int length() {
        return this.constructor.getParameterCount();
    }

    /**
     * Invoke the constructor
     * @param values the constructor values
     * @return the method result
     */
    @NotNull
    public Optional<T> invoke(final Object... values) {
        Class<?>[] params = this.constructor.getParameterTypes();
        if (params.length == 0) {
            try {
                return Optional.of(this.constructor.newInstance());
            } catch (ReflectiveOperationException ex) {
                return Optional.empty();
            }
        }

        Object[] paramValues = new Object[params.length];

        if (values == null || values.length == 0) {
            try {
                return Optional.of(this.constructor.newInstance(paramValues));
            } catch (ReflectiveOperationException ex) {
                return Optional.empty();
            }
        }

        for (int index = 0; index < params.length; index++) {
            Class<?> paramType = params[index];
            Object paramValue = null;
            for (Object value : values) {
                if (value == null) {
                    continue;
                }

                if (paramType.isInstance(value)) {
                    paramValue = value;
                    break;
                }
            }

            paramValues[index] = paramValue;
        }

        try {
            return Optional.of(this.constructor.newInstance(paramValues));
        } catch (ReflectiveOperationException ex) {
            return Optional.empty();
        }
    }
}
