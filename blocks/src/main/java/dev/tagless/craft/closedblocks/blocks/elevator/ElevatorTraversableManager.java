package dev.tagless.craft.closedblocks.blocks.elevator;

import dev.tagless.craft.closedblocks.blocks.Traversable;
import org.bukkit.World;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ElevatorTraversableManager {

    private static final Map<World, Map.Entry<int[], Traversable<BlockElevator>>> traversals = new ConcurrentHashMap<>();

    public static Traversable<BlockElevator> get(final World world, final int x, final int z) {
        return traversals.computeIfAbsent(world, ignored -> Map.entry(new int[]{x, z}, new Traversable<>(
                Comparator.comparingInt(BlockElevator::getY)
        ))).getValue();
    }
}
