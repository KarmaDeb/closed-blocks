package dev.tagless.craft.closedblocks.api.extension.feature;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;

/**
 * Generic interface for any ClosedBlock
 * feature. Allows to permit {@link ClosedBlocksIntegration integrations} to
 * disable specific (or all) features it
 * provides
 */
public interface ClosedBlocksFeature {

    /**
     * Get if the current feature is
     * enabled. By default, it will always be
     * true unless explicitly specified
     * @return if the feature is enabled
     */
    boolean isEnabled();
}
