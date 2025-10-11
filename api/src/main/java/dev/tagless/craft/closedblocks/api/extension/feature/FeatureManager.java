package dev.tagless.craft.closedblocks.api.extension.feature;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The feature manager allows for an integration
 * to manage its features
 */
public interface FeatureManager {

    /**
     * Get the feature manager integration
     * @return the integration of the feature
     * manager
     */
    @NotNull
    ClosedBlocksIntegration getIntegration();

    /**
     * Registers a feature. The feature will generate a
     * unique name based on {@link ClosedBlocksIntegration#getName()} and
     * the class simple name in snake_case. For instance,
     * integration "HelloWorld" registering a {@link dev.tagless.craft.closedblocks.api.block.EvolvedBlock} which
     * class name is "AmazingStoneBlock", would register the
     * feature as follows:
     * "helloworld:amazing_stone_block"
     * In case of name conflict (two features with the same name),
     * the feature will be registered with a suffix, randomly
     * generated
     * "helloworld:amazing_stone_block_(suffix: holy_cow)"
     * @param feature the feature to register
     */
    void register(final @NotNull Class<? extends ClosedBlocksFeature> feature);

    /**
     * Enables a feature
     * @param feature the feature class
     */
    void enable(final @NotNull Class<? extends ClosedBlocksFeature> feature);

    /**
     * Enables a feature, which at the same time, depends
     * on a sub-set of features. An example of this are
     * {@link dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior behaviors}, as they are a feature
     * which, depends directly on a {@link dev.tagless.craft.closedblocks.api.block.EvolvedBlock block}, which at the same
     * time, are a feature.
     * <pre>
     * {@code
     *     @IntendedFor(MyBlock.class)
     *     class MyBehavior extends BlockBehavior {
     *
     *     }
     *
     *     class MyBlock implements EvolvedBlock {
     *
     *     }
     *
     *     featureManager.enableFor(MyBehavior.class, MyBlock.class, AnotherBlock.class); //Enables "MyBehavior" on "MyBlock" only
     * }
     * </pre>
     * Another example:
     * <pre>
     * {@code
     *      @IntendedFor(MyBlock.class)
     *      class BehaviorOne extends BlockBehavior {
     *
     *      }
     *
     *      @IntendedFor(MyBlock.class)
     *      class BehaviorTwo extends BlockBehavior {
     *
     *      }
     *
     *      @IntendedFor(MyBlock.class)
     *      class BehaviorThree extends BlockBehavior {
     *
     *      }
     *
     *      class MyBlock implements EvolvedBlock {
     *
     *      }
     *
     *      featureManager.enableFor(MyBlock.class, BehaviorOne.class, BehaviorTwo.class, BehaviorThree.class);
     * }
     * </pre>
     */
    default void enableFor(final @NotNull Class<? extends ClosedBlocksFeature> feature,
                           final @NotNull Class<? extends ClosedBlocksFeature> feature1,
                           final Class<?>... features) {

    }

    /**
     * Disables a feature
     * @param feature the feature class
     */
    void disable(final @NotNull Class<? extends ClosedBlocksFeature> feature);

    /**
     * Get if a feature is provided by this
     * feature manager
     * @param feature the feature class
     * @return if the feature is provided by
     * this manager
     */
    boolean hasFeature(final @NotNull Class<? extends ClosedBlocksFeature> feature);

    /**
     * Get if the feature is enabled
     * @param feature the feature class
     * @return if the feature is globally enabled
     */
    boolean isEnabled(final @NotNull Class<? extends ClosedBlocksFeature> feature);

    /**
     * Disables all the features
     */
    void disable();

    /**
     * Enables all the features
     */
    void enable();

    /**
     * Get a block feature
     * @param key the feature name
     * @return the block feature
     */
    @NotNull
    Optional<Class<? extends ClosedBlocksFeature>> getFeature(final @NotNull NamespacedKey key);
}