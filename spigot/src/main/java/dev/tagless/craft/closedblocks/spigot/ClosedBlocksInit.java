package dev.tagless.craft.closedblocks.spigot;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.IntegrationState;
import dev.tagless.craft.closedblocks.api.extension.IntegrationStateReason;
import dev.tagless.craft.closedblocks.common.block.BlockRegistry;
import dev.tagless.craft.closedblocks.common.exception.ServiceRegistrationException;
import dev.tagless.craft.closedblocks.common.integration.IntegrationInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;

public class ClosedBlocksInit {

    private final ClosedBlocksSpigotAPI api;
    private Collection<IntegrationInfo> integrationInfos;

    public ClosedBlocksInit(final ClosedBlocksSpigotAPI api) {
        this.api = api;
    }

    public void scanIntegrations() {
        File serverDirectory = Bukkit.getWorldContainer();
        File pluginsDirectory = new File(serverDirectory, "plugins");

        integrationInfos = this.api.getStateManager().collectIntegrations(pluginsDirectory);
    }

    public void registerServices() {
        if (Bukkit.getServicesManager().load(EvolvedBlockRegistry.class) != null) {
            throw new ServiceRegistrationException(EvolvedBlockRegistry.class);
        }

        BlockRegistry blockRegistry = new BlockRegistry();
        Bukkit.getServicesManager().register(EvolvedBlockRegistry.class, blockRegistry, this.api.getPlugin(), ServicePriority.Highest);
    }

    public void loadIntegrations() {
        for (IntegrationInfo info : integrationInfos) {
            Plugin matchingPlugin = Bukkit.getPluginManager().getPlugin(info.plugin().getName());
            if (matchingPlugin == null) {
                this.api.getPlugin().getLogger().log(Level.WARNING,
                        "Not loading integration {} because its plugin ({}) has not been loaded",
                        new Object[]{info.integrationName(), info.plugin().getName()});
                continue;
            }
            this.api.getPlugin().getLogger().log(Level.INFO, "Loading integration {}",
                    new Object[]{info.integrationName()});

            this.api.getStateManager().createIntegration(info).ifPresent(integration -> {
                this.api.getStateManager().initIntegration(integration, matchingPlugin);
                this.api.getStateManager().setState(integration, IntegrationState.LOADED);

                this.api.getIntegrationManager().add(integration);
            });
        }
        integrationInfos = null;

        // https://www.spigotmc.org/threads/how-to-make-plugin-load-after-all-plugins.284987/#post-2754465
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.api.getPlugin(), () -> {
            for (ClosedBlocksIntegration integration : this.api.getIntegrationManager().getIntegrations()) {
                this.api.getStateManager().setState(integration, IntegrationState.ENABLED);
                integration.enable(IntegrationStateReason.PLUGIN);
            }
        });
    }
}
