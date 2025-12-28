package dev.tagless.craft.closedblocks.api.bundle.locale;

import dev.tagless.craft.closedblocks.api.util.data.OptionalList;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

/**
 * A localization allows to display a message
 * on different languages
 */
public interface Localization {

    /**
     * Get the localization locale
     * @return the locale
     */
    @NotNull
    Locale locale();

    /**
     * Get a localization string
     * @param key the localization key
     * @return the localization
     */
    @NotNull
    Optional<String> getString(final @NotNull String key);

    /**
     * Get a localization string list
     * @param key the localization key
     * @return the localization
     */
    @NotNull
    OptionalList<String> getStringList(final @NotNull String key);
}
