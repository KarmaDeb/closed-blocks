package dev.tagless.craft.closedblocks.api.block.evolution.behaviour;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A proxied block behavior serves as a
 * proxy for one or multiple block behaviors,
 * allowing to have a "single" block behavior
 * that behaves as more than a single behavior
 * @param <T> the block behavior type to be proxied,
 *           can be another ProxiedBlockBehavior, a BlockBehavior
 *           or any other type which extends BlockBehavior
 */
public final class ProxiedBlockBehavior<T extends BlockBehavior> extends BlockBehavior {

    private final Collection<T> behaviors;

    /**
     * Creates a new behavior
     *
     * @param block the behavior block
     * @param behaviors the behaviors to be proxied
     */
    public ProxiedBlockBehavior(final EvolvedBlock block, final Collection<? extends Class<? extends T>> behaviors) {
        super(block);
        this.behaviors = behaviors.stream().map(t -> {
            try {
                Constructor<? extends T> b = t.getConstructor(EvolvedBlock.class);
                return b.newInstance(block);
            } catch (ReflectiveOperationException ignored) {}

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
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
        return this.behaviors.stream().allMatch(b -> b.test(player));
    }

    /**
     * Play the behavior. If the behavior does not
     * require a player, the value could be null
     *
     * @param players the player to play the behavior to
     */
    @Override
    public void play(Player... players) {
        for (BlockBehavior behavior : this.behaviors) {
            if (behavior.isEnabled()) {
                behavior.play(players);
            }
        }
    }

    /**
     * Play the behavior.
     *
     * @param players the players to play the behavior to
     */
    @Override
    public void play(final @NotNull Collection<? extends Player> players) {
        this.play(players.toArray(new Player[0]));
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
        return this.behaviors.stream().allMatch(BlockBehavior::isEnabled);
    }

    /**
     * Get all the block behaviors that have
     * been proxied
     * @return the proxied behaviors
     */
    @NotNull
    public Collection<T> getBehaviors() {
        return this.behaviors;
    }
}
