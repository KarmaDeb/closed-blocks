package dev.tagless.craft.closedblocks.api.block.evolution.behaviour.sound;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.util.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block which behavior involves
 * playing a sound to a player
 */
public abstract class BlockSoundBehavior extends BlockBehavior {

    /**
     * Creates a new behavior
     * @param block the behavior block
     */
    public BlockSoundBehavior(final EvolvedBlock block) {
        super(block);
    }

    /**
     * Get the sound
     * @return the sound
     */
    @NotNull
    public abstract NamespacedKey getSound();

    /**
     * Get the volume
     * @return the volume
     */
    public abstract float getVolume();

    /**
     * Get the pitch
     * @return the pitch
     */
    public abstract float getPitch();

    /**
     * Play the behavior. If the behavior does not
     * require a player, the value could be null
     *
     * @param players the player to play the behavior to
     */
    @Override
    public final void play(final Player... players) {
        if (players == null) {
            return;
        }

        for (Player player : players) {
            Utils.sound().emit(player, this.getSound(), this.getPitch(), this.getVolume());
        }
    }
}
