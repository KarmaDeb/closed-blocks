package dev.tagless.craft.closedblocks.spigot.elevator.event;

import dev.tagless.craft.closedblocks.common.nbt.PersistentStore;
import dev.tagless.craft.closedblocks.spigot.elevator.ElevatorIntegration;
import dev.tagless.craft.closedblocks.spigot.elevator.integration.BlockElevator;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
public final class BlockEvents implements Listener {

    private final ElevatorIntegration integration;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Block block = event.getBlock();
        integration.getElevator(block).ifPresent(elevator -> {
            if (!elevator.destroy(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand())) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK) ||
                !handItem.getType().isAir() ||
                !player.isSneaking()) {
            return;
        }

        integration.getElevator(block).ifPresent(elevator -> {
            if (!elevator.destroy(player, handItem)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(final BlockBurnEvent event) {
        Block block = event.getBlock();
        integration.getElevator(block).ifPresent(elevator -> elevator.remove(true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onElevatorGriefByEnderman(final EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Enderman) {
            Block block = event.getBlock();
            event.setCancelled(integration.getElevator(block).isPresent());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAffectedByBlockExplosion(final BlockExplodeEvent event) {
        event.blockList().forEach(b ->
                integration.getElevator(b).ifPresent(elevator -> elevator.remove(true)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAffectedByEntityExplosion(final EntityExplodeEvent event) {
        event.blockList().forEach(b ->
                integration.getElevator(b).ifPresent(elevator -> elevator.remove(true)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!block.getType().isAir()) {
            return;
        }

        ItemStack stack = event.getItemInHand();
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        
        PersistentStore itemStore = PersistentStore.detect(stack);
        if (!itemStore.getString("type").orElse("").equals("elevator")) {
            return;
        }

        BlockElevator elevator = new BlockElevator(player.getUniqueId(), block);
        elevator.setMaterial(stack.getType().getKeyOrThrow());
        if (elevator.place(player, block)) {
            //TODO: Send a message
            event.setBuild(false);
            event.setCancelled(true);
        }

        //TODO: Send a message
    }


}