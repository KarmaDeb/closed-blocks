package dev.tagless.craft.closedblocks.api.util.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;

/**
 * A toggleable reference is an object that
 * stores a toggleable reference to a key-value.
 * The toggleable does not apply any special
 * behavior internally; instead, it's expected to
 * be used externally. For instance, a toggleable
 * reference could be used to enable or disable worlds
 * which are identified with a generated NamespacedKey
 * <pre>
 * {@code
 *      ToggleableReference<UUID, World> map = new ToggleableReference(world -> NamespacedKey.fromString("world:" + world.getName()));
 *      map.add(someWorld);
 *      map.set(someWorld, true);
 *
 *      status = map.get(someWorld);
 *      NamespacedKey reference = map.getReference(someWorld);
 * }
 * </pre>
 * @param <K> the reference key
 * @param <V> the value
 */
public class ToggleableReference<K, V> {

    private final Function<V, K> keyGenerator;

    private final Map<K, Map.Entry<V, Boolean>> toggleable = new HashMap<>();
    private final Map<V, K> references = new HashMap<>();

    private final Bulk<K, V> bulkInstance = new Bulk<>(this);

    /**
     * Create a new toggleable reference
     * @param keyGenerator the key generator
     */
    public ToggleableReference(final @NotNull Function<V, K> keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    /**
     * Create a new toggleable reference
     */
    public ToggleableReference() {
        this.keyGenerator = null;
    }

    /**
     * Adds a value
     * @param key the value key
     * @param value the value
     */
    public void add(final @NotNull K key, final @NotNull V value) {
        if (this.keyGenerator != null) {
            throw new IllegalArgumentException("Tried to call add with key on a key independent toggleable reference instance");
        }

        this.addValue(key, value);
    }

    /**
     * Adds a value
     * @param value the value to add
     */
    public void add(final @NotNull V value) {
        if (this.keyGenerator == null) {
            throw new IllegalArgumentException("Tried to call add without key on a key dependant toggleable reference instance");
        }

        K key = this.keyGenerator.apply(value);
        this.addValue(key, value);
    }

    private void addValue(final @NotNull K key, final @NotNull V value) {
        if (this.references.containsValue(key)) {
            return;
        }

        this.toggleable.put(key, Map.entry(value, true));
        this.references.put(value, key);
    }

    /**
     * Get if the toggleable contains the
     * specified key
     * @param key the key
     * @return if the key is contained
     */
    public boolean hasKey(final @NotNull K key) {
        return this.toggleable.containsKey(key);
    }

    /**
     * Get if the toggleable contains the
     * specified value
     * @param value the value
     * @return if the value is contained
     */
    public boolean hasValue(final @NotNull V value) {
        return this.references.containsKey(value);
    }

    /**
     * Set a status
     * @param key the toggleable key
     * @param status the new status
     */
    public void set(final @NotNull V key, final boolean status) {
        K reference = this.references.get(key);
        if (reference == null) {
            return;
        }

        this.toggleable.get(reference).setValue(status);
    }

    /**
     * Get the status of a toggleable
     * @param key the key of the toggleable
     * @return the toggleable value
     */
    public boolean get(final @NotNull V key) {
        K reference = this.references.get(key);
        if (reference == null) {
            return false;
        }

        return this.toggleable.get(reference).getValue();
    }

    /**
     * Get a reference to a key
     * @param value the key value
     * @return the value reference
     */
    @NotNull
    public Optional<K> getReference(final @NotNull V value) {
        return Optional.ofNullable(this.references.get(value));
    }

    /**
     * Get a value from a reference key
     * @param key the value key
     * @return the value
     */
    @NotNull
    public Optional<V> getValue(final @NotNull K key) {
        if (!this.toggleable.containsKey(key)) {
            return Optional.empty();
        }

        Map.Entry<V, Boolean> entry = this.toggleable.get(key);
        return Optional.of(entry.getKey());
    }

    /**
     * Get all the values in the toggleable reference
     * @return the values
     */
    @NotNull @Unmodifiable
    public Collection<V> getValues() {
        return Collections.unmodifiableCollection(this.references.keySet());
    }

    /**
     * Get the toggleable bulk instance
     * operator
     * @return the bulk operator
     */
    public Bulk<K, V> bulk() {
        return this.bulkInstance;
    }

    /**
     * The bulk instance allows performing
     * bulk operations for a toggleable instance
     * @param <K> the toggleable key type
     * @param <V> the toggleable value type
     */
    public static final class Bulk<K, V> {

        private final ToggleableReference<K, V> reference;

        /**
         * Create a new bulk editor
         * @param reference the editor reference
         */
        private Bulk(final @NotNull ToggleableReference<K, V> reference) {
            this.reference = reference;
        }

        /**
         * Bulk add to the toggleable instance
         * @param first the first element
         * @param values the extra elements
         */
        @SafeVarargs
        public final void add(final @NotNull V first, final V... values) {
            this.reference.add(first);
            if (values != null) {
                for (V value : values) {
                    this.reference.add(value);
                }
            }
        }

        /**
         * Bulk enable the toggleable instance
         * values. Or empty for all
         * @param values the values to enable
         */
        @SafeVarargs
        public final void enable(final V... values) {
            if (values == null || values.length == 0) {
                for (V  value : this.reference.references.keySet()) {
                    this.reference.set(value, true);
                }
                return;
            }

            for (V value : values) {
                this.reference.set(value, true);
            }
        }

        /**
         * Bulk disable the toggleable instance
         * values. Or empty for all
         * @param values the values to disable
         */
        @SafeVarargs
        public final void disable(final V... values) {
            if (values == null || values.length == 0) {
                for (V  value : this.reference.references.keySet()) {
                    this.reference.set(value, false);
                }
                return;
            }

            for (V value : values) {
                this.reference.set(value, false);
            }
        }
    }
}
