package dev.tagless.craft.closedblocks.api.localization;

import org.jetbrains.annotations.NotNull;

/**
 * A localization allows to display a message
 * on different languages
 */
public interface Localization {

    /**
     * Get the localization name
     * @return the name of the localization
     */
    @NotNull
    String getName();

    /**
     * Get the localization display name
     * @return the display name of the localization
     */
    @NotNull
    String getDisplayName();
}
