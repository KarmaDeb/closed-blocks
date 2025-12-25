package dev.tagless.craft.closedblocks.api.bundle;

import dev.tagless.craft.closedblocks.api.util.data.OptionalList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;

/**
 * A configuration allows to obtain a setting
 * for a feature
 */
@SuppressWarnings("unused")
public interface Configuration {

    /**
     * Get a section
     * @param key the section key
     * @return the configuration section
     */
    @NotNull
    Optional<Configuration> getSection(final String key);

    /**
     * Get a string
     * @param key the string key
     * @return the configuration value
     */
    @NotNull
    Optional<String> getString(final String key);

    /**
     * Get a number
     * @param key the number key
     * @return the configuration value
     */
    @NotNull
    Optional<Number> getNumber(final String key);

    /**
     * Get a boolean
     * @param key the boolean key
     * @return the configuration value
     */
    @NotNull
    Optional<Boolean> getBoolean(final String key);

    /**
     * Get a list of strings
     * @param key the list key
     * @return the configuration value
     */
    @NotNull @Unmodifiable
    OptionalList<String> getStringList(final String key);

    /**
     * Get a list of numbers
     * @param key the list key
     * @return the configuration value
     */
    @NotNull @Unmodifiable
    OptionalList<Number> getNumberList(final String key);

    /**
     * Get a list of booleans
     * @param key the list key
     * @return the configuration value
     */
    @NotNull @Unmodifiable
    OptionalList<Boolean> getBooleanList(final String key);

    /**
     * Get a list of sections
     * @param key the list key
     * @return the configuration value
     */
    @NotNull @Unmodifiable
    OptionalList<Configuration> getSectionList(final String key);
}
