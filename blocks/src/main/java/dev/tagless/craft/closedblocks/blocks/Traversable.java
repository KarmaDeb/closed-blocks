package dev.tagless.craft.closedblocks.blocks;

import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public class Traversable<T extends EvolvedBlock> {

    private final NavigableSet<T> path;

    private final Map<T, Integer> indexes = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int displayOffset = 0;

    public Traversable(final Comparator<T> comparator) {
        this.path = new ConcurrentSkipListSet<>(comparator);
    }

    public boolean add(final T block) {
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

    public boolean remove(final T block) {
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

    public void setDisplayIndex(T block, int displayIndex) {
        lock.writeLock().lock();
        try {
            Integer idx = indexes.get(block);
            if (idx == null) return;
            this.displayOffset = displayIndex - idx;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int index(final T block) {
        lock.readLock().lock();
        try {
            return indexes.getOrDefault(block, -1);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int displayIndex(final T block) {
        lock.readLock().lock();
        try {
            int idx = indexes.getOrDefault(block, -1);
            return idx == -1 ? -1 : idx + displayOffset;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<T> next(final T block) {
        return Optional.ofNullable(this.path.higher(block));
    }

    public Optional<T> previous(final T block) {
        return Optional.ofNullable(this.path.lower(block));
    }

    public Optional<T> step(final T block, final int steps) {
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