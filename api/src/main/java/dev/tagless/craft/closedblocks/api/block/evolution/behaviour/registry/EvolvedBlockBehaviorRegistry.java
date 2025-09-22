package dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry;

import dev.tagless.craft.closedblocks.api.ClosedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The evolved block behavior registry allows registering
 * custom {@link BlockBehavior behaviors} for different
 * {@link EvolvedBlock blocks}
 */
public interface EvolvedBlockBehaviorRegistry extends ClosedBlockRegistry {

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
     * @param type the block class
     * @return the block behavior registry
     * @param <T> the block type
     */
    @NotNull
    <T extends EvolvedBlock> Optional<? extends BlockBehaviorRegistry> get(final @NotNull Class<T> type);
}
