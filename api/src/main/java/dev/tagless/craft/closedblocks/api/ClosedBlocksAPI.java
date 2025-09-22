package dev.tagless.craft.closedblocks.api;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegrationManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The ClosedBlockAPI allows interacting
 * directly with the API of the plugin
 */
public abstract class ClosedBlocksAPI {

    private static ClosedBlocksAPI instance;

    /**
     * Create a new API instance
     */
    protected ClosedBlocksAPI() {
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * Get the plugin integration manager
     * @return the integration manager
     */
    @NotNull
    public abstract ClosedBlocksIntegrationManager getIntegrationManager();

    /**
     * Get a plugin registry
     * @param type the registry type
     * @return the plugin registry
     * @param <T> the type of the registry
     */
    @NotNull
    public abstract <T extends ClosedBlockRegistry> Optional<T> getRegistry(final Class<T> type);

    /**
     * Get the ClosedBlocks API instance
     * @return the API instance
     */
    @NotNull
    public static ClosedBlocksAPI getInstance() {
        return Objects.requireNonNull(instance, "Cannot obtain ClosedBlocks API while the plugin has not been initialized");
    }
}
