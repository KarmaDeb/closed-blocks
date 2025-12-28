package dev.tagless.craft.closedblocks.common.integration;

import dev.tagless.craft.closedblocks.api.bundle.locale.LocalizationBundle;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.IntegrationState;
import dev.tagless.craft.closedblocks.common.i18n.IntegrationLocalizationBundle;
import dev.tagless.craft.closedblocks.common.integration.exception.PartialIntegrationException;
import dev.tagless.craft.closedblocks.common.integration.feature.IntegrationFeatureManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class IntegrationStateManager {

    private final Logger logger;

    public IntegrationStateManager(final @NotNull Logger logger) {
        this.logger = logger;
    }

    @NotNull @Unmodifiable
    public Collection<IntegrationInfo> collectIntegrations(final @NotNull File directory) {
        if (!directory.exists()) {
            return Collections.emptyList();
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        Collection<IntegrationInfo> infoCollection = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }

            String name = file.getName();
            if (!name.matches("^.*?\\.jar$")) {
                continue;
            }

            try {
                IntegrationInfo info = IntegrationMetadataReader.readInfo(file).orElse(null);
                if (info == null) {
                    continue;
                }

                infoCollection.add(info);
            } catch (IOException ignored) {} catch (PartialIntegrationException ex) {
                logger.warn("Found valid closed blocks integration \"{}\", but missing a plugin. Integrations require of a plugin to work",
                        ex.getIntegration());
            }
        }

        return Collections.unmodifiableCollection(infoCollection);
    }

    public Optional<ClosedBlocksIntegration> createIntegration(final @NotNull IntegrationInfo info) {
        try {
            Class<?> clazz = Class.forName(info.integrationClass());
            if (!ClosedBlocksIntegration.class.isAssignableFrom(clazz)) {
                this.logger.error("Failed to load integration {}. Provided class ({}) is not an instance of {}",
                        info.integrationName(), info.integrationClass(), ClosedBlocksIntegration.class.getName());
                return Optional.empty();
            }

            Class<? extends ClosedBlocksIntegration> integrationClass = clazz.asSubclass(ClosedBlocksIntegration.class);

            Constructor<? extends ClosedBlocksIntegration> constructor = integrationClass.getConstructor();
            ClosedBlocksIntegration integration = constructor.newInstance();

            Field nameField = ClosedBlocksIntegration.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(integration, info.integrationName());
            nameField.setAccessible(false);

            return Optional.of(integration);
        } catch (ReflectiveOperationException ex) {
            this.logger.error("Failed to create integration {} ({})", info.integrationName(), info.integrationClass());
        }

        return Optional.empty();
    }

    public void setState(final @NotNull ClosedBlocksIntegration integration, final @NotNull IntegrationState state) {
        try {
            Field stateField = ClosedBlocksIntegration.class.getDeclaredField("state");
            stateField.setAccessible(true);
            stateField.set(integration, IntegrationState.LOADED);
            stateField.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            this.logger.error("Failed to update integration state to {} on {}", state, integration.getName());
        }
    }

    public void initIntegration(final @NotNull ClosedBlocksIntegration integration, final @NotNull Plugin plugin) {
        try {
            updateField(integration, "featureManager",
                    new IntegrationFeatureManager(integration));
            updateField(integration, "plugin", plugin);

            Logger l = LoggerFactory.getLogger(plugin.getClass());
            updateField(integration, "logger", l);

            LocalizationBundle bundle = IntegrationLocalizationBundle.empty(integration);
            try {
                bundle = new IntegrationLocalizationBundle(integration);
            } catch (IOException ex) {
                setState(integration, IntegrationState.DEGRADED);
                this.logger.atWarn().setCause(ex)
                        .log("Failed to initialize localization for integration {}", integration.getName());
            }

            updateField(integration, "localization", bundle);
        } catch (ReflectiveOperationException ex) {
            this.logger.error("Failed to initialize integration {}", integration.getName());
        }
    }

    private void updateField(final ClosedBlocksIntegration integration, final String field, final Object value)
            throws ReflectiveOperationException {
        Field f = ClosedBlocksIntegration.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(integration, value);
        f.setAccessible(false);
    }
}