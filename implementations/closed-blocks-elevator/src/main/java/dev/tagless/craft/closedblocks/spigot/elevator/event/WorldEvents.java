package dev.tagless.craft.closedblocks.spigot.elevator.event;

import dev.tagless.craft.closedblocks.spigot.elevator.ElevatorIntegration;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

@RequiredArgsConstructor
public final class WorldEvents implements Listener {

    private final ElevatorIntegration integration;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldLoaded(final WorldLoadEvent event) {
        World world = event.getWorld();
        integration.loadElevators(world);
    }
}
