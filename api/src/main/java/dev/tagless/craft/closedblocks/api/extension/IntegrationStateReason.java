package dev.tagless.craft.closedblocks.api.extension;

/**
 * The integration state reason provides
 * all the reasons an integration might have
 * been disabled or enabled.
 */
public enum IntegrationStateReason {
    /**
     * The integration has updated its state because
     * of a command.
     * <ul>
     *     <li>Integration load command run</li>
     *     <li>Integration unload command run</li>
     * </ul>
     */
    COMMAND,

    /**
     * The integration has updated its state because
     * of ClosedBlocks plugin request.
     * <ul>
     *     <li>ClosedBlocks plugin enabled</li>
     *     <li>ClosedBlocks plugin disabled</li>
     * </ul>
     */
    PLUGIN
}
