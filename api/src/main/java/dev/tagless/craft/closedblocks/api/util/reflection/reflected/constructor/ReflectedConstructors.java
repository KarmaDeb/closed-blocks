package dev.tagless.craft.closedblocks.api.util.reflection.reflected.constructor;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * A reflected collection of constructors
 * @param <T> the constructor type
 */
public class ReflectedConstructors<T> implements Iterable<ReflectedConstructor<T>> {

    private final String label;
    private final Collection<ReflectedConstructor<T>> constructors;

    /**
     * Create a new reflected constructor
     * @param label the constructor label
     * @param constructors the constructors
     */
    @SafeVarargs
    public ReflectedConstructors(final @NotNull String label,
                                 final @NotNull ReflectedConstructor<T> ...constructors) {
        this.label = label;
        this.constructors = Arrays.asList(constructors);
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
     * Get the amount of constructors
     * @return the amount of parameters
     */
    public int length() {
        return this.constructors.size();
    }

    /**
     * Invoke the constructor
     * @param values the constructor values
     * @return the method result
     */
    @NotNull
    public Optional<T> invoke(final Object... values) {
        for (ReflectedConstructor<T> constructor : this.constructors) {
            Optional<T> result = constructor.invoke(values);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    /**
     * Invoke the constructor
     * @param values the constructor values
     * @return the method result
     */
    @NotNull
    public Optional<T> invokes(final Object[]... values) {
        for (Object[] params : values) {
            Optional<T> result = this.invoke(params);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public @NotNull Iterator<ReflectedConstructor<T>> iterator() {
        return this.constructors.iterator();
    }
}
