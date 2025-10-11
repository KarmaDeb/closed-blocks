package dev.tagless.craft.closedblocks.api.extension.feature;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generic interface for any ClosedBlock
 * feature. Allows to permit {@link ClosedBlocksIntegration integrations} to
 * disable specific (or all) features it
 * provides. A feature can be design in a way
 * it only supports a sub-set of features, by
 * combining it with the {@link IntendedFor} annotation.
 * This annotation creates a contract between two (or more)
 * features in where only those features can be used
 * between them.
 * <pre>
 *     {@code
 *     interface FeatureThree extends ClosedBlocksFeature {
 *
 *     }
 *
 *     interface FeatureTwo extends ClosedBlocksFeature {
 *
 *     }
 *
 *     @IntendedFor(FeatureTwo.class)
 *     interface FeatureOne extends ClosedBlocksFeature {
 *
 *
 *     }
 *
 *     main() {
 *         ClosedBlocksFeature.canUse(FeatureOne.class, FeatureTwo.class); //True
 *         ClosedBlocksFeature.canUse(FeatureTwo.class, FeatureOne.class); //True
 *         ClosedBlocksFeature.canUse(FeatureOne.class, FeatureThree.class); //False
 *         ClosedBlocksFeature.canUse(FeatureThree.class, FeatureOne.class); //False
 *         ClosedBlocksFeature.canUse(FeatureTwo.class, FeatureThree.class); //False
 *         ClosedBlocksFeature.canUse(FeatureThree.class, FeatureTwo.class); //False
 *     }
 *
 *     }
 * </pre>
 */
public interface ClosedBlocksFeature {

    /**
     * Get if the current feature is
     * enabled. By default, it will always be
     * true unless explicitly specified
     * @return if the feature is enabled
     */
    boolean isEnabled();

    /**
     * The intended for annotation specifies that, a
     * block feature, is exclusively intended for a
     * sub-set of block feature types. This creates a
     * virtual "dependency" tree in where a feature,
     * works only on some features, and so on
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface IntendedFor {

        /**
         * Get the feature this feature was intended
         * for
         * @return the feature intended for
         */
        @NotNull
        Class<? extends ClosedBlocksFeature>[] value();
    }

    /**
     * Get if the specified feature can be used by
     * another
     * @param user the feature which "uses" the feature
     * @param service the feature which would be "used"
     * @return if the "user" can use the "service"
     */
    static boolean canUse(final @NotNull Class<? extends ClosedBlocksFeature> user,
                          final @NotNull Class<? extends ClosedBlocksFeature> service) {
        IntendedFor intendedFor = service.getAnnotation(IntendedFor.class);
        if (intendedFor == null) {
            intendedFor = user.getAnnotation(IntendedFor.class);
            if (intendedFor == null || intendedFor.value().length == 0) {
                return true;
            }

            for (Class<? extends ClosedBlocksFeature> feature : intendedFor.value()) {
                if (feature.isAssignableFrom(service)) {
                    return true;
                }
            }

            return false;
        }

        if (intendedFor.value().length == 0) {
            return true;
        }

        for (Class<? extends ClosedBlocksFeature> feature : intendedFor.value()) {
            if (feature.isAssignableFrom(user)) {
                return true;
            }
        }

        return false;
    }
}