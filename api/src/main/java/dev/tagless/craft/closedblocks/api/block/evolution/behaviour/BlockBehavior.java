package dev.tagless.craft.closedblocks.api.block.evolution.behaviour;

import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.extension.feature.ClosedBlocksFeature;
import dev.tagless.craft.closedblocks.api.annotation.FinalDefaultImplementation;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Represents a behavior of a block
 */
public abstract class BlockBehavior implements ClosedBlocksFeature, Predicate<Player> {

    protected final EvolvedBlock block;

    /**
     * Creates a new behavior
     * @param block the behavior block
     */
    public BlockBehavior(final @NotNull EvolvedBlock block) {
        this.block = block;
    }

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
        return true;
    }

    /**
     * Play the behavior. If the behavior does not
     * require a player, the value could be null
     * @param players the player to play the behavior to
     */
    public abstract void play(final Player... players);

    /**
     * Play the behavior.
     * @param players the players to play the behavior to
     */
    @FinalDefaultImplementation
    public void play(final @NotNull Collection<? extends Player> players) {
        if (players.isEmpty()) {
            play();
            return;
        }

        play(players.toArray(new Player[0]));
    }

    /**
     * Get if the current feature is
     * enabled. By default, it will always be
     * true unless explicitly specified
     *
     * @return if the feature is enabled
     */
    @Override
    public boolean isEnabled() {
        return ClosedBlocksAPI.getInstance().getRegistry(EvolvedBlockBehaviorRegistry.class)
                .flatMap(registry -> registry.get(this.block.getClass()))
                .map(r ->  r.isEnabled(this.getClass()))
                .orElse(false);
    }
}
