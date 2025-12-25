package dev.tagless.craft.closedblocks.spigot;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.BlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.IntegrationState;
import dev.tagless.craft.closedblocks.api.extension.IntegrationStateReason;
import dev.tagless.craft.closedblocks.api.material.MaterialLookupService;
import dev.tagless.craft.closedblocks.common.block.BlockRegistry;
import dev.tagless.craft.closedblocks.common.block.behavior.BlockBehaviorsRegistry;
import dev.tagless.craft.closedblocks.common.exception.ServiceRegistrationException;
import dev.tagless.craft.closedblocks.common.integration.IntegrationInfo;
import dev.tagless.craft.closedblocks.common.lookup.MaterialsLookup;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.function.Supplier;
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
        this.addService(EvolvedBlockRegistry.class, this.api.getRegistry(BlockRegistry.class)::orElseThrow);
        this.addService(EvolvedBlockBehaviorRegistry.class,
                this.api.getRegistry(EvolvedBlockBehaviorRegistry.class)::orElseThrow);
        this.addService(BlockBehaviorRegistry.class, BlockBehaviorsRegistry::new);
        this.addService(MaterialLookupService.class, () -> new MaterialsLookup(this.api.getPlugin()));
    }

    private <T> void addService(final Class<T> service, final Supplier<? extends T> provider) {
        if (Bukkit.getServicesManager().load(service) != null) {
            throw new ServiceRegistrationException(service);
        }

        String svcName = service.getSimpleName();
        try {
            Field serviceName = service.getDeclaredField("SERVICE_NAME");
            serviceName.setAccessible(true);

            svcName = String.valueOf(serviceName.get(service));
        } catch (ReflectiveOperationException ignored) {}

        this.api.getPlugin().getLogger().log(
                Level.INFO,
                "Registering {} as a Bukkit service",
                svcName
        );

        T instance = provider.get();
        Bukkit.getServicesManager().register(service, instance, this.api.getPlugin(), ServicePriority.Highest);
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