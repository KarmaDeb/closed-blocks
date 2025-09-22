package dev.tagless.craft.closedblocks.common.integration.exception;

import org.bukkit.plugin.InvalidDescriptionException;

public class PartialIntegrationException extends InvalidDescriptionException {

    private final String integration;

    public PartialIntegrationException(final InvalidDescriptionException cause, String integration) {
        super(cause, String.format("Refusing to load integration %s", integration));
        this.integration = integration;
    }

    public String getIntegration() {
        return this.integration;
    }
}
