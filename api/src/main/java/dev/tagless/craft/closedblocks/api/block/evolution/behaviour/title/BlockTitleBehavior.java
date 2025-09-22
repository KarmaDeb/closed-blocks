package dev.tagless.craft.closedblocks.api.block.evolution.behaviour.title;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block which behavior involves
 * sending a title to a player
 */
public abstract class BlockTitleBehavior extends BlockBehavior {

    /**
     * Creates a new behavior
     * @param block the behavior block
     */
    public BlockTitleBehavior(final EvolvedBlock block) {
        super(block);
    }

    /**
     * Get the title text
     * @return the text
     */
    @NotNull
    public abstract String getTitle();

    /**
     * Get the title subtitle
     * @return the subtitle
     */
    @NotNull
    public abstract String getSubtitle();

    /**
     * Get the title fade in time
     * @return the fade in
     */
    public abstract int getFadeIn();

    /**
     * Get the title stay time
     * @return the stay
     */
    public abstract int getStay();

    /**
     * Get the title fade out time
     * @return the fade out
     */
    public abstract int getFadeOut();

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
            player.sendTitle(
                    Utils.string().color(this.getTitle()),
                    Utils.string().color(this.getSubtitle()),
                    this.getFadeIn(),
                    this.getStay(),
                    this.getFadeOut()
            );
        }
    }
}
