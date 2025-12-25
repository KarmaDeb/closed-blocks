package dev.tagless.craft.closedblocks.api.bundle;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A localization allows to display a message
 * on different languages
 */
@SuppressWarnings("unused")
public interface Localization {

    /**
     * Get the localization locale
     * @return the locale
     */
    @NotNull
    Locale getLocale();

    /**
     * Get a localization string
     * @param key the localization key
     * @return the localization
     */
    Optional<String> getString(final String key);

    /**
     * Get a localization string list
     * @param key the localization key
     * @return the localization
     */
    List<String> getStringList(final String key);
}
