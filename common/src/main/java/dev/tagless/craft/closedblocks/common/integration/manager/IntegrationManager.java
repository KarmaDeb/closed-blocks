package dev.tagless.craft.closedblocks.common.integration.manager;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegrationManager;
import dev.tagless.craft.closedblocks.api.extension.IntegrationState;
import dev.tagless.craft.closedblocks.common.integration.IntegrationStateManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class IntegrationManager implements ClosedBlocksIntegrationManager {

    private final IntegrationStateManager stateManager;
    private final Collection<ClosedBlocksIntegration> integrations = ConcurrentHashMap.newKeySet();

    public IntegrationManager(final @NotNull IntegrationStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void add(final @NotNull ClosedBlocksIntegration integration) {
        integrations.add(integration);
    }

    /**
     * Enable an integration
     *
     * @param integration the integration to enable
     */
    @Override
    public void enable(final @NotNull ClosedBlocksIntegration integration) {
        if (!this.integrations.contains(integration) || !integration.getState().equals(IntegrationState.DISABLED)) {
            return;
        }

        stateManager.setState(integration, IntegrationState.ENABLED);
    }

    /**
     * Disables an integration
     *
     * @param integration the integration to disable
     */
    @Override
    public void disable(final @NotNull ClosedBlocksIntegration integration) {
        if (!this.integrations.contains(integration) || !integration.getState().equals(IntegrationState.ENABLED)) {
            return;
        }

        stateManager.setState(integration, IntegrationState.DISABLED);
    }

    /**
     * Get if an integration is enabled
     *
     * @param integration the integration to check for
     * @return if the integration is enabled
     */
    @Override
    public boolean isEnabled(final @NotNull ClosedBlocksIntegration integration) {
        if (!this.integrations.contains(integration)) {
            return false;
        }

        return integration.getState().equals(IntegrationState.ENABLED);
    }

    /**
     * Get an integration by plugin. If the plugin is an
     * instance of {@link ClosedBlocksIntegration integration} itself,
     * the cast instance will be returned
     *
     * @param plugin the plugin registering the integration
     * @return the integration
     */
    @Override
    public @NotNull Optional<? extends ClosedBlocksIntegration> fromPlugin(final @NotNull Plugin plugin) {
        if (plugin instanceof ClosedBlocksIntegration integration) {
            if (this.integrations.contains(integration)) {
                return Optional.of(integration);
            }

            return Optional.empty();
        }

        return this.integrations
                .stream()
                .filter(integration -> integration.getPlugin().equals(plugin))
                .findFirst();
    }

    /**
     * Get all the registered integrations
     *
     * @return the plugin integrations
     */
    @Override
    public @NotNull @Unmodifiable Collection<? extends ClosedBlocksIntegration> getIntegrations() {
        return Collections.unmodifiableCollection(this.integrations);
    }
}