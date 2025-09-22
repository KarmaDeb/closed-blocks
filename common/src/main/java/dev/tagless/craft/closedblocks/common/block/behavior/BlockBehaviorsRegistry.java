package dev.tagless.craft.closedblocks.common.block.behavior;

import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.BlockBehaviorRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockBehaviorsRegistry implements BlockBehaviorRegistry {

    private final Map<Class<? extends BlockBehavior>, Boolean> behaviorMap = new ConcurrentHashMap<>();

    /**
     * Registers a behavior
     *
     * @param behavior the behavior to register
     */
    @Override
    public <T extends BlockBehavior> void register(final @NotNull Class<T> behavior) {
        if (this.behaviorMap.containsKey(behavior)) {
            return;
        }

        this.behaviorMap.put(behavior, true);
    }

    /**
     * Enables a behavior
     *
     * @param behavior the behavior to enable
     */
    @Override
    public <T extends BlockBehavior> void enable(final @NotNull Class<T> behavior) {
        if (!this.behaviorMap.containsKey(behavior)) {
            for (Map.Entry<Class<? extends BlockBehavior>, Boolean> entry : this.behaviorMap.entrySet()) {
                if (behavior.isAssignableFrom(entry.getKey())) {
                    entry.setValue(true);
                    return;
                }
            }

            return;
        }

        this.behaviorMap.put(behavior, true);
    }

    /**
     * Disables a behavior
     *
     * @param behavior the behavior to disable
     */
    @Override
    public <T extends BlockBehavior> void disable(final @NotNull Class<T> behavior) {
        if (!this.behaviorMap.containsKey(behavior)) {
            for (Map.Entry<Class<? extends BlockBehavior>, Boolean> entry : this.behaviorMap.entrySet()) {
                if (behavior.isAssignableFrom(entry.getKey())) {
                    entry.setValue(false);
                    return;
                }
            }

            return;
        }

        this.behaviorMap.put(behavior, false);
    }

    /**
     * Get if a behavior is enabled
     *
     * @param behavior the behavior to check for
     * @return if the behavior is enabled
     */
    @Override
    public <T extends BlockBehavior> boolean isEnabled(final @NotNull Class<T> behavior) {
        if (!this.behaviorMap.containsKey(behavior)) {
            for (Map.Entry<Class<? extends BlockBehavior>, Boolean> entry : this.behaviorMap.entrySet()) {
                if (behavior.isAssignableFrom(entry.getKey())) {
                    return entry.getValue();
                }
            }

            return false;
        }

        return this.behaviorMap.get(behavior);
    }

    /**
     * Enables the entire block behavior
     */
    @Override
    public void enable() {
        this.behaviorMap.replaceAll((k, v) -> true);
    }

    /**
     * Disables the entire block behavior
     */
    @Override
    public void disable() {
        this.behaviorMap.replaceAll((k, v) -> false);
    }

    /**
     * Get all the block behaviors
     *
     * @return the behaviors of the block
     */
    @Override @Unmodifiable
    public @NotNull Collection<? extends Class<? extends BlockBehavior>> getAll() {
        return Collections.unmodifiableCollection(this.behaviorMap.keySet());
    }
}
