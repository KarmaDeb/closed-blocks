package dev.tagless.craft.closedblocks.common.block.behavior;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.BlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.common.block.BlockRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BehaviorRegistry implements EvolvedBlockBehaviorRegistry {

    private final BlockRegistry blockRegistry;
    private final Map<Class<? extends EvolvedBlock>, BlockBehaviorsRegistry> registryMap = new ConcurrentHashMap<>();

    public BehaviorRegistry(final @NotNull BlockRegistry blockRegistry) {
        this.blockRegistry = blockRegistry;
    }

    /**
     * Get the behavior registry. If the type provided
     * is literally the class of {@link EvolvedBlock} or
     * the literal class of a façade of an evolved block.
     * The registered behaviors will be then available for
     * any subclass of that façade. If an implementation class
     * is instead used, the registered behavior will be only
     * available for instances of that implementation class (and
     * any of its subclasses if any). The block is required to
     * exist in the {@link EvolvedBlockRegistry registry}, otherwise this
     * method will return an empty optional
     *
     * @param type the block class
     * @return the block behavior registry
     */
    @Override
    public <T extends EvolvedBlock> @NotNull Optional<BlockBehaviorRegistry> get(final @NotNull Class<T> type) {
        if (this.blockRegistry.isRegistered(type)) {
            return Optional.of(this.registryMap.computeIfAbsent(type, ignored -> new BlockBehaviorsRegistry()));
        }

        return Optional.empty();
    }

    /**
     * Get all the behavior registries in where a block behavior
     * has been registered
     *
     * @param feature the feature to lookup for
     * @return all the registries which contains the feature
     */
    @Override
    public @NotNull @Unmodifiable Collection<? extends BlockBehaviorRegistry> getRegistries(final @NotNull Class<? extends BlockBehavior> feature) {
        return this.registryMap.values()
                .stream()
                .filter(registry -> registry.isRegistered(feature))
                .collect(Collectors.toUnmodifiableSet());
    }
}
