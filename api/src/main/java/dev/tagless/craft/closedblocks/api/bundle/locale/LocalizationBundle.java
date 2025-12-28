package dev.tagless.craft.closedblocks.api.bundle.locale;

import dev.tagless.craft.closedblocks.api.util.data.OptionalList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/**
 * The localization bundle provides all the
 * integration localizations
 */
public interface LocalizationBundle extends Localization {

    /**
     * Get a locale of the bundle
     * @param locale the locale to get for
     * @return the bundle locale
     * @throws IllegalStateException if there's no localization for the
     * provided locale
     */
    @NotNull
    Localization get(final @Nullable Locale locale) throws IllegalStateException;

    /**
     * Get the localization locale
     *
     * @return the locale
     */
    @Override
    default @NotNull Locale locale() {
        return Locale.getDefault();
    }

    /**
     * Get a localization string
     *
     * @param key the localization key
     * @return the localization
     */
    @Override
    default @NotNull Optional<String> getString(final @NotNull String key) {
        try {
            Localization localization = this.get(null);
            return localization.getString(key);
        } catch (IllegalStateException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Get a localization string list
     *
     * @param key the localization key
     * @return the localization
     */
    @Override
    default @NotNull OptionalList<String> getStringList(final @NotNull String key) {
        try {
            Localization localization = this.get(null);
            return localization.getStringList(key);
        } catch (IllegalStateException ignored) {
            return OptionalList.empty();
        }
    }
}