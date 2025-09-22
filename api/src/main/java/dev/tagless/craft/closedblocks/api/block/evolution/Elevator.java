package dev.tagless.craft.closedblocks.api.block.evolution;

import dev.tagless.craft.closedblocks.api.BlockEvolution;
import dev.tagless.craft.closedblocks.api.annotation.FinalDefaultImplementation;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The elevator represents the most basic
 * block provided by the plugin. A block which, when
 * "connected" to others of the same kind, can be
 * used to fast travel between different layers on
 * any construction
 */
public interface Elevator extends EvolvedBlock, Navigable {

    /**
     * Get the block evolution type
     *
     * @return the block evolution type
     */
    @Override @FinalDefaultImplementation
    default @NotNull BlockEvolution getEvolution() {
        return BlockEvolution.ELEVATOR;
    }

    /**
     * Get the current floor name. Can be empty,
     * in which case, and if enabled, nothing
     * will be shown in the title screen when
     * using the elevator except for the
     * floor number
     * @return the floor number
     */
    @NotNull
    String getFloorName();

    /**
     * Set the current floor name. Can be empty
     * @param value the floor name
     */
    void setFloorName(final @NotNull String value);

    /**
     * Get the current floor level. Usually matches
     * {@link Navigable#getCurrent()}, but this is not
     * a requirement for Elevators.
     * @return the floor level of the
     * elevator
     */
    default int getDisplayFloor() {
        return this.getCurrent();
    }

    /**
     * Set the current floor level. By default, this
     * method does nothing because of the nature of
     * {@link #getDisplayFloor()}. Changing a floor
     * number will not change the block index
     * @param index the display floor number
     */
    default void setDisplayFloor(final int index) {}

    /**
     * Teleports the specified player into the
     * elevator
     * @param player the player to teleport
     * @return if the player could be teleported
     */
    @FinalDefaultImplementation
    default boolean use(final @NotNull Player player) {
        Optional<Block> blockOptional = this.getBlock();
        BlockBehavior behavior = this.getBehavior();

        if (blockOptional.isEmpty() || !behavior.test(player)) {
            return false;
        }

        Block block = blockOptional.get();

        Location playerLocation = player.getLocation();
        Location blockLocation = block.getLocation().clone();

        blockLocation.setYaw(playerLocation.getYaw());
        blockLocation.setPitch(playerLocation.getPitch());
        blockLocation.setDirection(playerLocation.getDirection());

        blockLocation.add(0.5, 0.5, 0.5);

        behavior.play(player);

        return player.teleport(blockLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Get the previous block in the navigation sequence
     *
     * @return the previous block
     */
    @Override
    @NotNull Optional<? extends Elevator> previous();

    /**
     * Get the next block in the navigation sequence
     *
     * @return the next block
     */
    @Override
    @NotNull Optional<? extends Elevator> next();

    /**
     * Get the next block in the navigation sequence. If the
     * amount is negative, the sequence will be "played"
     * backwards
     *
     * @param number the number of steps to take
     * @return the next or previous block in the
     * specified steps
     */
    @Override
    @NotNull Optional<? extends Elevator> step(final int number);
}