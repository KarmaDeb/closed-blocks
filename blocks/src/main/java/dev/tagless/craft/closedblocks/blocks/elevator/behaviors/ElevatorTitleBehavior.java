package dev.tagless.craft.closedblocks.blocks.elevator.behaviors;

import dev.tagless.craft.closedblocks.api.block.evolution.Elevator;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.title.BlockTitleBehavior;
import org.jetbrains.annotations.NotNull;

public class ElevatorTitleBehavior extends BlockTitleBehavior {

    private final Elevator block;

    /**
     * Creates a new behavior
     *
     * @param block the behavior block
     */
    public ElevatorTitleBehavior(final Elevator block) {
        super(block);
        this.block = block;
    }

    /**
     * Get the title text
     *
     * @return the text
     */
    @Override
    public @NotNull String getTitle() {
        return this.block.getFloorName();
    }

    /**
     * Get the title subtitle
     *
     * @return the subtitle
     */
    @Override
    public @NotNull String getSubtitle() {
        return String.valueOf(this.block.getDisplayFloor());
    }

    /**
     * Get the title fade in time
     *
     * @return the fade in
     */
    @Override
    public int getFadeIn() {
        return 0;
    }

    /**
     * Get the title stay time
     *
     * @return the stay
     */
    @Override
    public int getStay() {
        return 20 * 3;
    }

    /**
     * Get the title fade out time
     *
     * @return the fade out
     */
    @Override
    public int getFadeOut() {
        return 0;
    }
}
