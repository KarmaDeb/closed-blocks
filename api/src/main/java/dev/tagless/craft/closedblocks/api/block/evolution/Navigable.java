package dev.tagless.craft.closedblocks.api.block.evolution;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A navigable block DOES NOT represent a block itself,
 * but a block which can be navigated "frontwards" and
 * "backwards". Being "frontwards" always up, or east, west,
 * north or south, depending on context, and "backwards" always
 * down or east, west, north or south depending on context.
 */
public interface Navigable {

    /**
     * Get the minimum index of the navigation sequence. Usually
     * zero, but there's no restriction on it.
     * @return the minimum index
     */
    int getMin();

    /**
     * Get the current index of the navigation sequence.
     * @return the current index
     */
    int getCurrent();

    /**
     * Get the maximum index of the navigation sequence. Usually
     * the number of blocks in the sequence, but there's no
     * restriction on it.
     * @return the maximum index
     */
    int getMax();

    /**
     * Get the previous block in the navigation sequence
     * @return the previous block
     */
    @NotNull
    Optional<? extends Navigable> previous();

    /**
     * Get the next block in the navigation sequence
     * @return the next block
     */
    @NotNull
    Optional<? extends Navigable> next();

    /**
     * Get the next block in the navigation sequence. If the
     * amount is negative, the sequence will be "played"
     * backwards
     * @param number the number of steps to take
     * @return the next or previous block in the
     * specified steps
     */
    @NotNull
    Optional<? extends Navigable> step(final int number);
}
