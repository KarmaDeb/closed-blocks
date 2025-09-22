package dev.tagless.craft.closedblocks.api.extension;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

/**
 * The ClosedBlock integration manager does not allow
 *  registering or unregister existing integrations, but
 * allows enabling, disable and list them
 */
public interface ClosedBlocksIntegrationManager {

    /**
     * Enable an integration
      * @param integration the integration to enable
     */
    void enable(final @NotNull ClosedBlocksIntegration integration);

    /**
     * Disables an integration
     * @param integration the integration to disable
     */
    void disable(final @NotNull ClosedBlocksIntegration integration);

    /**
     * Get if an integration is enabled
     * @param integration the integration to check for
     * @return if the integration is enabled
     */
    boolean isEnabled(final @NotNull ClosedBlocksIntegration integration);

    /**
     * Get an integration by plugin. If the plugin is an
     * instance of {@link ClosedBlocksIntegration integration} itself,
     * the cast instance will be returned
     * @param plugin the plugin registering the integration
     * @return the integration
     */
    @NotNull
    Optional<? extends ClosedBlocksIntegration> fromPlugin(final @NotNull Plugin plugin);

    /**
     * Get all the registered integrations
     * @return the plugin integrations
     */
    @NotNull @Unmodifiable
    Collection<? extends ClosedBlocksIntegration> getIntegrations();
}
