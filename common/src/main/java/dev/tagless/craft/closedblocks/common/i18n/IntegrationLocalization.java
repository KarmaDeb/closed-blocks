package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.bundle.locale.Localization;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.util.data.OptionalList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public record IntegrationLocalization(ClosedBlocksIntegration integration,
                                      Locale locale,
                                      PropertyResourceBundle prb) implements Localization {

    /**
     * Get the localization locale
     *
     * @return the locale
     */
    @Override
    public @NotNull Locale locale() {
        return this.locale;
    }

    /**
     * Get a localization string
     *
     * @param key the localization key
     * @return the localization
     */
    @Override
    public @NotNull Optional<String> getString(final @NotNull String key) {
        if (!this.prb.containsKey(key)) {
            this.integration.getLogger().log(Level.WARNING, "Missing localization key {} for locale {}",
                    new Object[]{
                            key,
                            this.locale.toLanguageTag()
                    });

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
    public @NotNull OptionalList<String> getStringList(final @NotNull String key) {
        if (!this.prb.containsKey(key)) {
            this.integration.getLogger().log(Level.WARNING, "Missing localization key {} for locale {}",
                    new Object[]{
                            key,
                            this.locale.toLanguageTag()
                    });

            return OptionalList.empty();
        }

        return OptionalList.collect(
                Arrays.asList(this.prb.getStringArray(key))
        );
    }
}