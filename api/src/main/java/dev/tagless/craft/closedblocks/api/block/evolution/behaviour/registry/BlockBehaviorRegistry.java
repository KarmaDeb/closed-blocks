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
     */
    void register(final @NotNull Class<? extends BlockBehavior> behavior);

    /**
     * Enables a behavior
     * @param behavior the behavior to enable
     */
    void enable(final @NotNull Class<? extends BlockBehavior> behavior);

    /**
     * Disables a behavior
     * @param behavior the behavior to disable
     */
    void disable(final @NotNull Class<? extends BlockBehavior> behavior);

    /**
     * Get if a behavior is enabled
     * @param behavior the behavior to check for
     * @return if the behavior is enabled
     */
    boolean isEnabled(final @NotNull Class<? extends BlockBehavior> behavior);

    /**
     * Get if a behavior is registered
     * @param behavior the behavior to check for
     */
    boolean isRegistered(final @NotNull Class<? extends BlockBehavior> behavior);

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
