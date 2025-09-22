package dev.tagless.craft.closedblocks.api.extension.exception;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;

/**
 * The not initialized exception is thrown when a value
 * of {@link ClosedBlocksIntegration integration} is tried to be
 * accessed but the integration has not been yet initialized
 * (but might have been loaded)
 */
public class NotInitializedException extends RuntimeException {

    /**
     * Initialize the exception
     * @param integration the integration affected
     * @param featureName the feature which was tried to be
     *                    accessed
     */
    public NotInitializedException(final ClosedBlocksIntegration integration, final String featureName) {
        super(String.format("Cannot access %s from %s because it has not been yet initialized", featureName, integration.getName()));
    }
}
