package dev.tagless.craft.closedblocks.spigot.elevator;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.IntegrationStateReason;
import dev.tagless.craft.closedblocks.common.nbt.PersistentStore;
import dev.tagless.craft.closedblocks.spigot.elevator.event.BlockEvents;
import dev.tagless.craft.closedblocks.spigot.elevator.event.WorldEvents;
import dev.tagless.craft.closedblocks.spigot.elevator.integration.BlockElevator;
import dev.tagless.craft.closedblocks.spigot.elevator.integration.behaviors.ElevatorTitleBehavior;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public final class ElevatorIntegration extends ClosedBlocksIntegration {

    private final BlockEvents blockEvents = new BlockEvents(this);
    private final WorldEvents worldEvents = new WorldEvents(this);

    /**
     * Enables the integration
     *
     * @param reason the reason of why the integration was enabled
     */
    @Override
    public void enable(final @NotNull IntegrationStateReason reason) {
        this.getFeatureManager().enableFor(ElevatorTitleBehavior.class, BlockElevator.class);

        Bukkit.getWorlds().forEach(this::loadElevators);

        Bukkit.getPluginManager().registerEvents(blockEvents, this);
        Bukkit.getPluginManager().registerEvents(worldEvents, this);
    }

    /**
     * Disables the integration
     *
     * @param reason the reason of why the integration was disabled
     */
    @Override
    public void disable(final @NotNull IntegrationStateReason reason) {
        this.getFeatureManager().disable(ElevatorTitleBehavior.class);
        this.getFeatureManager().disable(BlockElevator.class);

        HandlerList.unregisterAll(blockEvents);
        HandlerList.unregisterAll(worldEvents);
    }

    public Optional<BlockElevator> getElevator(final Block block) {
        PersistentStore store = PersistentStore.detect(block);
        String type = store.getString("type").orElse("");
        Optional<UUID> storeOwner = store.getUUID("owner");

        if (!type.equals("elevator") || storeOwner.isEmpty()) {
            return Optional.empty();
        }

        UUID ownerUUID = storeOwner.get();
        return Optional.of(new BlockElevator(ownerUUID, block));
    }

    public void loadElevators(final World world) {
        logger().info("[{}] Loading elevators for world {}",
                this.getName(), world.getName());

        PersistentStore store = PersistentStore.detect(world);
        store.getStringList("elevators").forEach(elevatorData -> {
            String[] data = elevatorData.split("_");
            int x = Integer.parseInt(data[0]);
            int y = Integer.parseInt(data[1]);
            int z = Integer.parseInt(data[2]);

            Block block = world.getBlockAt(x, y, z);
            getElevator(block); //This triggers loading...
        });
    }
}
