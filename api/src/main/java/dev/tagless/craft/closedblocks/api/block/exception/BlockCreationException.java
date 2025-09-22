package dev.tagless.craft.closedblocks.api.block.exception;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The block creation exception is thrown when
 * an unexpected error happens during the creation
 * of a block.
 */
public final class BlockCreationException extends Exception {

    /**
     * Create the exception
     * @param player the player who tried to place the block
     * @param block the block that was placed
     * @param type the block type
     */
    public BlockCreationException(final @NotNull Player player, final @NotNull Block block, final @NotNull Class<? extends EvolvedBlock> type) {
        super(String.format("Failed to create block %s at world %s (X: %d, Y: %d, Z: %d) for player %s",
                type.getSimpleName(),
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ(),
                player.getName()));
    }
}
