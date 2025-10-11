package dev.tagless.craft.closedblocks.api.block;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A traversable object represents a set of nodes
 * which are sorted in a specific way.
 * @param <T> the traversable block type
 */
@ThreadSafe
public final class Traversable<T extends EvolvedBlock> {

    private final NavigableSet<T> path;

    private final Map<T, Integer> indexes = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int displayOffset = 0;

    /**
     * Create a new traversable object
     * @param comparator the comparator for sorting
     */
    public Traversable(final @NotNull Comparator<T> comparator) {
        this.path = new ConcurrentSkipListSet<>(comparator);
    }

    /**
     * Adds a block
     * @param block the block to add
     * @return if the block was added
     */
    public boolean add(final @NotNull T block) {
        this.lock.writeLock().lock();

        try {
            if (!path.add(block)) {
                return false;
            }

            int pos = path.headSet(block).size();
            shiftIndexes(pos, 1);

            indexes.put(block, pos);
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Removes a block
     * @param block the block to remove
     * @return if the block was removed
     */
    public boolean remove(final @NotNull T block) {
        this.lock.writeLock().lock();

        try {
            Integer idx = indexes.remove(block);
            if (idx == null) return false;

            path.remove(block);
            shiftIndexes(idx, -1);
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Set a block display index. It's important to
     * note that, changing any block display's index will
     * also change its siblings recursively.
     * @param block the block
     * @param displayIndex the block display index
     */
    public void setDisplayIndex(final @NotNull T block, final int displayIndex) {
        lock.writeLock().lock();
        try {
            Integer idx = indexes.get(block);
            if (idx == null) return;
            this.displayOffset = displayIndex - idx;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get the index of a block. If the block
     * is not present, -1 is returned
     * @param block the block
     * @return the block index
     */
    public int index(final @NotNull T block) {
        lock.readLock().lock();
        try {
            return indexes.getOrDefault(block, -1);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the display index of a block. By default,
     * the same value as {@link #index(EvolvedBlock)} is
     * returned
     * @param block the block to obtain the display
     *              index for
     * @return the block display index
     */
    public int displayIndex(final @NotNull T block) {
        lock.readLock().lock();
        try {
            int idx = indexes.getOrDefault(block, -1);
            return idx == -1 ? -1 : idx + displayOffset;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the next block to the specified
     * one
     * @param block the block
     * @return the block next sibling
     */
    @NotNull
    public Optional<T> next(final @NotNull T block) {
        return Optional.ofNullable(this.path.higher(block));
    }

    /**
     * Get the previous block to the specified
     * one
     * @param block the block
     * @return the block previous sibling
     */
    @NotNull
    public Optional<T> previous(final @NotNull T block) {
        return Optional.ofNullable(this.path.lower(block));
    }

    /**
     * Get the block steps away from the specified
     * one. If the steps is negative, the iteration
     * will be performed backwards
     * @param block the block to obtain the sibling for
     * @param steps the amount of steps to take
     * @return the previous or next sibling
     */
    @NotNull
    public Optional<T> step(final @NotNull T block, final int steps) {
        T current = block;
        int remainingSteps = Math.abs(steps);

        while (remainingSteps-- > 0 && current != null) {
            if (steps > 0) {
                current = this.path.higher(current);
            } else {
                current = this.path.lower(current);
            }
        }

        return Optional.ofNullable(current);
    }

    private void shiftIndexes(final int start, final int delta) {
        indexes.replaceAll((ignored, idx) -> idx >= start ? idx + delta : idx);
    }

    public int getSize() {
        lock.readLock().lock();
        try {
            return path.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}