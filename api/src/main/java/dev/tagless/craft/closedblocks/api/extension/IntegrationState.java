package dev.tagless.craft.closedblocks.api.extension;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * The integration state represents the current
 * real state of an integration. This can be either
 * ENABLED, DISABLED or LOADED
 */
public enum IntegrationState {
    /**
     * The integration has been enabled. This
     * implicitly means that the integration is
     * also {@link #LOADED}
     */
    ENABLED(ChatColor.GREEN),
    /**
     * The integration has been disabled. This
     * implicitly means that the integration is
     * also {@link #LOADED}
     */
    DISABLED(ChatColor.RED),
    /**
     * The integration has been loaded. This
     * means that the integration has been loaded
     * into memory but has not been enabled or disabled
     */
    LOADED(ChatColor.AQUA),
    /**
     * The integration is not yet loaded. This state
     * is only present if the integration was instantiated
     * from another plugin which is not ClosedBlocks, and
     * the plugin has not yet loaded the integration
     */
    UNLOADED(ChatColor.GRAY),
    /**
     * The integration is completely loaded, but some features
     * are missing. Those features are not relevant to the main
     * extension to work, and calling them will not cause a crash.
     * But may not work as expected (if they work at all)
     */
    DEGRADED(ChatColor.YELLOW);

    private final ChatColor color;

    /**
     * Creates a new integration state
     * @param color the state color
     */
    IntegrationState(final @NotNull ChatColor color) {
        this.color = color;
    }

    /**
     * Get the integration color
     * @return the color of the integration
     * state
     */
    @SuppressWarnings("unused")
    public ChatColor getColor() {
        return this.color;
    }


    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum class should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return String.format("%s%s",
                this.color, this.name().toLowerCase());
    }
}
