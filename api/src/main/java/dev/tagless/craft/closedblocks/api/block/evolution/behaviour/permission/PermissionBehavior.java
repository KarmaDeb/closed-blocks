package dev.tagless.craft.closedblocks.api.block.evolution.behaviour.permission;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a block which behavior involves
 * verifying a player has permissions
 */
public abstract class PermissionBehavior extends BlockBehavior {

    /**
     * Creates a new behavior
     * @param block the behavior block
     */
    public PermissionBehavior(final EvolvedBlock block) {
        super(block);
    }

    /**
     * Get the permission to check for
     * @return the permission
     */
    @NotNull
    public abstract Permission getPermission();

    /**
     * Checks if the behavior blocks the normal flow
     * based on the player. This means that, if any block
     * behavior returns false in this method, the entire
     * block behavior will be stopped and canceled, preventing,
     * for instance, an elevator from sending a player up or
     * down.
     *
     * @param player the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final @NotNull Player player) {
        return player.hasPermission(this.getPermission());
    }

    /**
     * Play the behavior. If the behavior does not
     * require a player, the value could be null
     *
     * @param players the player to play the behavior to
     */
    @Override
    public final void play(final Player... players) {}

    /**
     * Play the behavior.
     *
     * @param players the players to play the behavior to
     */
    @Override
    public final void play(final @NotNull Collection<? extends Player> players) {}
}