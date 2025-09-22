package dev.tagless.craft.closedblocks.api.extension;

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
    ENABLED,
    /**
     * The integration has been disabled. This
     * implicitly means that the integration is
     * also {@link #LOADED}
     */
    DISABLED,
    /**
     * The integration has been loaded. This
     * means that the integration has been loaded
     * into memory but has not been enabled or disabled
     */
    LOADED,
    /**
     * The integration is not yet loaded. This state
     * is only present if the integration was instantiated
     * from another plugin which is not ClosedBlocks, and
     * the plugin has not yet loaded the integration
     */
    UNLOADED
}
