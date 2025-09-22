package dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry;

import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

/**
 * The block behavior registry contains all the
 * {@link BlockBehavior behaviors} a block can have
 */
public interface BlockBehaviorRegistry {

    /**
     * Registers a behavior
     * @param behavior the behavior to register
     * @param <T> the behavior type
     */
    <T extends BlockBehavior> void register(final @NotNull Class<T> behavior);

    /**
     * Enables a behavior
     * @param behavior the behavior to enable
     * @param <T> the behavior type
     */
    <T extends BlockBehavior> void enable(final @NotNull Class<T> behavior);

    /**
     * Disables a behavior
     * @param behavior the behavior to disable
     * @param <T> the behavior type
     */
    <T extends BlockBehavior> void disable(final @NotNull Class<T> behavior);

    /**
     * Get if a behavior is enabled
     * @param behavior the behavior to check for
     * @return if the behavior is enabled
     * @param <T> the behavior type
     */
    <T extends BlockBehavior> boolean isEnabled(final @NotNull Class<T> behavior);

    /**
     * Enables the entire block behavior
     */
    void enable();

    /**
     * Disables the entire block behavior
     */
    void disable();

    /**
     * Get all the block behaviors
     * @return the behaviors of the block
     */
    @NotNull @Unmodifiable
    Collection<? extends Class<? extends BlockBehavior>> getAll();
}
