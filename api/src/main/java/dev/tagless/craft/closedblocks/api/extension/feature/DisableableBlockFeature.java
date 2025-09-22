package dev.tagless.craft.closedblocks.api.extension.feature;

/**
 * A disableable block feature is a feature, which
 * can be disabled
 */
public interface DisableableBlockFeature extends ClosedBlocksFeature {

    /**
     * Set the feature status
     * @param status the status of this feature
     *               instance
     */
    void setEnabled(final boolean status);
}
