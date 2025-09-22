package dev.tagless.craft.closedblocks.common.integration.feature;

import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.BlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.feature.ClosedBlocksFeature;
import dev.tagless.craft.closedblocks.api.extension.feature.FeatureManager;
import dev.tagless.craft.closedblocks.api.util.Utils;
import dev.tagless.craft.closedblocks.api.util.data.ToggleableReference;
import dev.tagless.craft.closedblocks.common.block.BlockRegistry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IntegrationFeatureManager implements FeatureManager {

    private final ClosedBlocksIntegration integration;

    private final ToggleableReference<NamespacedKey, Class<? extends ClosedBlocksFeature>> featureMap = new ToggleableReference<>(this::generateFeatureKey);

    public IntegrationFeatureManager(final ClosedBlocksIntegration integration) {
        this.integration = integration;
    }

    /**
     * Get the feature manager integration
     *
     * @return the integration of the feature
     * manager
     */
    @Override
    public @NotNull ClosedBlocksIntegration getIntegration() {
        return this.integration;
    }

    /**
     * Registers a feature. The feature will generate a
     * unique name based on {@link ClosedBlocksIntegration#getName()} and
     * the class simple name in snake_case. For instance,
     * integration "HelloWorld" registering a {@link EvolvedBlock} which
     * class name is "AmazingStoneBlock", would register the
     * feature as follows:
     * "helloworld:amazing_stone_block"
     * In case of name conflict (two features with the same name),
     * the feature will be registered with a suffix, randomly
     * generated
     * "helloworld:amazing_stone_block_(suffix: holy_cow)"
     *
     * @param feature the feature to register
     */
    @Override
    public void register(final @NotNull Class<? extends ClosedBlocksFeature> feature) {
        if (this.featureMap.hasValue(feature)) {
            return;
        }

        this.featureMap.add(feature);

        if (EvolvedBlock.class.isAssignableFrom(feature)) {
            BlockRegistry registry = this.getEvolvedBlockRegistry();
            registry.register(this.integration, feature.asSubclass(EvolvedBlock.class));
        }
    }

    /**
     * Enables a feature
     *
     * @param feature the feature class
     */
    @Override
    public void enable(final @NotNull Class<? extends ClosedBlocksFeature> feature) {
        if (!this.featureMap.hasValue(feature) || this.featureMap.hasValue(feature)) {
            return;
        }

        this.featureMap.set(feature, true);
        if (EvolvedBlock.class.isAssignableFrom(feature)) {
            EvolvedBlockRegistry registry = this.getEvolvedBlockRegistry();
            EvolvedBlockBehaviorRegistry behaviorRegistry = this.getEvolvedBlockBehaviorRegistry();

            registry.enable(feature.asSubclass(EvolvedBlock.class));
            behaviorRegistry
                    .get(feature.asSubclass(EvolvedBlock.class))
                    .ifPresent(BlockBehaviorRegistry::disable);
        }
    }

    /**
     * Disables a feature
     *
     * @param feature the feature class
     */
    @Override
    public void disable(final @NotNull Class<? extends ClosedBlocksFeature> feature) {
        if (!this.featureMap.hasValue(feature) || !this.featureMap.get(feature)) {
            return;
        }

        this.featureMap.set(feature, false);
        if (EvolvedBlock.class.isAssignableFrom(feature)) {
            EvolvedBlockRegistry blockRegistry = this.getEvolvedBlockRegistry();
            EvolvedBlockBehaviorRegistry behaviorRegistry = this.getEvolvedBlockBehaviorRegistry();

            blockRegistry.disable(feature.asSubclass(EvolvedBlock.class));
            behaviorRegistry
                    .get(feature.asSubclass(EvolvedBlock.class))
                    .ifPresent(BlockBehaviorRegistry::disable);
        }
    }

    /**
     * Get if a feature is provided by this
     * feature manager
     *
     * @param feature the feature class
     * @return if the feature is provided by
     * this manager
     */
    @Override
    public boolean hasFeature(final @NotNull Class<? extends ClosedBlocksFeature> feature) {
        return this.featureMap.hasValue(feature);
    }

    /**
     * Get if the feature is enabled
     *
     * @param feature the feature class
     * @return if the feature is globally enabled
     */
    @Override
    public boolean isEnabled(final @NotNull Class<? extends ClosedBlocksFeature> feature) {
        return this.featureMap.get(feature);
    }

    /**
     * Disables all the features
     */
    @Override
    public void disable() {
        this.featureMap.bulk().disable();
    }

    /**
     * Enables all the features
     */
    @Override
    public void enable() {
        this.featureMap.bulk().enable();
    }

    /**
     * Get a block feature
     *
     * @param key the feature name
     * @return the block feature
     */
    @Override
    public @NotNull Optional<Class<? extends ClosedBlocksFeature>> getFeature(final NamespacedKey key) {
        return this.featureMap.getValue(key);
    }

    private NamespacedKey generateFeatureKey(final Class<? extends ClosedBlocksFeature> feature) {
        if (this.featureMap.hasValue(feature)) {
            throw new IllegalArgumentException("Feature already exists");
        }

        String key = String.format("%s:%s", this.integration.getName().toLowerCase(), Utils.string().toSnakeCase(feature.getSimpleName()));
        NamespacedKey nsk = NamespacedKey.fromString(key);
        if (nsk == null) {
            throw new IllegalArgumentException("Invalid NamespacedKey format: " + key);
        }

        if (this.featureMap.hasKey(nsk)) {
            nsk = NamespacedKey.fromString(String.format("%s_%s", nsk.getKey(), Utils.string().getRandomString()));
        }
        if (nsk == null || this.featureMap.hasKey(nsk)) {
            throw new IllegalArgumentException("Failed to generate a valid NamespacedKey for feature: " + feature.getSimpleName());
        }

        return nsk;
    }

    @NotNull
    public BlockRegistry getEvolvedBlockRegistry() {
        return ClosedBlocksAPI.getInstance()
                .getRegistry(BlockRegistry.class)
                .orElseThrow(() -> new IllegalStateException("EvolvedBlockRegistry is not available"));
    }

    @NotNull
    public EvolvedBlockBehaviorRegistry getEvolvedBlockBehaviorRegistry() {
        return ClosedBlocksAPI.getInstance()
                .getRegistry(EvolvedBlockBehaviorRegistry.class)
                .orElseThrow(() -> new IllegalStateException("EvolvedBlockBehaviorRegistry is not available"));
    }
}