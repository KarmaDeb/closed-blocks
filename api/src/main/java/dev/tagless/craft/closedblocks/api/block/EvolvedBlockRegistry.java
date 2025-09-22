package dev.tagless.craft.closedblocks.api.block;

import dev.tagless.craft.closedblocks.api.ClosedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.exception.BlockCreationException;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

/**
 * The evolved block registry allows
 * registering and obtain {@link EvolvedBlock evolved blocks}
 * easily. Any plugin can use its own blocks without
 * registering them, although this is not recommended
 * at all. When registering a block through an integration
 * with the {@link dev.tagless.craft.closedblocks.api.extension.feature.FeatureManager#register(Class) register method}, this
 * operation is performed completely automatically, as well as
 * disabling it also disables the block in the registry.
 * Integrations allow automatizing a lot of processes that are
 * highly recommended when interacting with ClosedBlocks API.
 * It's also important to note that passing abstract or interface
 * classes in the register method will result in the action not
 * performing.
 */
public interface EvolvedBlockRegistry extends ClosedBlockRegistry {

    /**
     * Registers a new block.
     * @param block the block to register
     * @param <T> the block type
     */
    <T extends EvolvedBlock> void register(final @NotNull Class<T> block);

    /**
     * Get if a block is registered
     * @param block the block
     * @return if the block is registered
     */
    boolean isRegistered(final @NotNull Class<? extends EvolvedBlock> block);

    /**
     * Get if a block is enabled
     * @param block the block to check
     * @return if the block is enabled
     */
    boolean isEnabled(final @NotNull Class<? extends EvolvedBlock> block);

    /**
     * Enables the block. If the block is an interface
     * or an abstract class, any other block implementing
     * or extending it will be also disabled
     * @param block the block to enable
     */
    void enable(final @NotNull Class<? extends EvolvedBlock> block);

    /**
     * Disables the block. If the block is an interface
     * or an abstract class, any other block implementing
     * or extending it will be also disabled
     * @param block the block to disable
     */
    void disable(final @NotNull Class<? extends EvolvedBlock> block);

    /**
     * Add a block placement
     * @param type the block type to place
     * @param player the player who is placing the block
     * @param block the block to place
     * @return the placed block
     * @param <T> the block type
     * @throws BlockCreationException if there's a problem while creating
     * the block
     */
    @NotNull
    <T extends EvolvedBlock> Optional<T> place(final @NotNull Class<T> type, final @NotNull Player player, final @NotNull Block block) throws BlockCreationException;

    /**
     * Get all the registered blocks
     * @return the blocks in the registry
     */
    @NotNull @Unmodifiable
    Collection<Class<? extends EvolvedBlock>> getRegisteredBlocks();

    /**
     * Get a block by its name
     * @param key the block name
     * @return the block
     */
    @NotNull
    Optional<Class<? extends EvolvedBlock>> getBlock(final @NotNull NamespacedKey key);
}
