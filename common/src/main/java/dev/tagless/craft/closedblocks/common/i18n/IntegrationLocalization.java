package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.bundle.Localization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@AllArgsConstructor
public class IntegrationLocalization implements Localization {

    private final Locale locale;
    private final PropertyResourceBundle prb;

    /**
     * Get the localization locale
     *
     * @return the locale
     */
    @Override
    public @NotNull Locale getLocale() {
        return this.locale;
    }

    /**
     * Get a localization string
     *
     * @param key the localization key
     * @return the localization
     */
    @Override
    public Optional<String> getString(final String key) {
        if (!this.prb.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(this.prb.getString(key));
    }

    /**
     * Get a localization string list
     *
     * @param key the localization key
     * @return the localization
     */
    @Override
    public List<String> getStringList(final String key) {
        if (!this.prb.containsKey(key)) {
            return Collections.emptyList();
        }

        return Arrays.asList(this.prb.getStringArray(key));
    }
}
