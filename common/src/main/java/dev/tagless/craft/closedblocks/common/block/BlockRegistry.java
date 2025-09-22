package dev.tagless.craft.closedblocks.common.block;

import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.exception.BlockCreationException;
import dev.tagless.craft.closedblocks.api.util.Utils;
import dev.tagless.craft.closedblocks.api.util.data.ToggleableReference;
import dev.tagless.craft.closedblocks.api.util.reflection.ReflectedConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Path;
import java.util.*;

public class BlockRegistry implements EvolvedBlockRegistry {

    private final ToggleableReference<NamespacedKey, Class<? extends EvolvedBlock>> blockMap = new ToggleableReference<>();

    /**
     * Registers a new block.
     * @param block the block to register
     * @param <T> the block type
     */
    @Override
    public <T extends EvolvedBlock> void register(final @NotNull Class<T> block) {
        Utils.stack().getExternalCaller().ifPresent(caller -> {
            Path file = Utils.stack().getClassDeclaringFile(caller);
            Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .filter(plugin -> Utils.stack().getClassDeclaringFile(plugin.getClass()).equals(file))
                    .findFirst()
                    .ifPresent(plugin -> this.register(plugin, block));
        });
    }

    /**
     * Get if a block is registered
     *
     * @param block the block
     * @return if the block is registered
     */
    @Override
    public boolean isRegistered(final @NotNull Class<? extends EvolvedBlock> block) {
        if (this.blockMap.hasValue(block)) {
            return true;
        }

        for (Class<? extends EvolvedBlock> b : this.blockMap.getValues()) {
            if (block.isAssignableFrom(b)) {
                return true;
            }
        }

        return false;
    }

    @ApiStatus.Internal
    public <T extends EvolvedBlock> void register(final Plugin plugin, final Class<T> block) {
        if (this.blockMap.hasValue(block)) {
            return;
        }

        ClosedBlocksAPI api = ClosedBlocksAPI.getInstance();
        Plugin p = api.getIntegrationManager()
                .fromPlugin(plugin)
                .map(a -> (Plugin) a)
                .orElse(plugin);

        NamespacedKey key = this.generateBlockKey(p, block);
        this.blockMap.add(key, block);
    }

    /**
     * Get if a block is enabled
     *
     * @param block the block to check
     * @return if the block is enabled
     */
    @Override
    public boolean isEnabled(final @NotNull Class<? extends EvolvedBlock> block) {
        return this.blockMap.get(block);
    }

    /**
     * Enables the block. If the block is an interface
     * or an abstract class, any other block implementing
     * or extending it will be also disabled
     *
     * @param block the block to enable
     */
    @Override
    public void enable(final @NotNull Class<? extends EvolvedBlock> block) {
        this.blockMap.set(block, true);
    }

    /**
     * Disables the block. If the block is an interface
     * or an abstract class, any other block implementing
     * or extending it will be also disabled
     *
     * @param block the block to disable
     */
    @Override
    public void disable(final @NotNull Class<? extends EvolvedBlock> block) {
        this.blockMap.set(block, false);
    }

    /**
     * Add a block placement
     *
     * @param type   the block type to place
     * @param player the player who is placing the block
     * @param block  the block to place
     * @return the placed block
     * @throws BlockCreationException if there's a problem while creating the
     * block
     */
    @Override
    public @NotNull <T extends EvolvedBlock> Optional<T> place(final @NotNull Class<T> type, final @NotNull Player player, final @NotNull Block block) throws BlockCreationException {
        if (!this.blockMap.hasValue(type)) {
            return Optional.empty();
        }

        ReflectedConstructor<T> constructor =
                Utils.reflection()
                        .findConstructor("player_world", type, Player.class, Block.class)
                        .or(() -> Utils.reflection()
                                .findConstructor("player_unpacked_block", type, Player.class, World.class, int.class, int.class, int.class))
                        .or(() -> Utils.reflection()
                                .findConstructor("player_raw_unpacked_block", type, Player.class, UUID.class, int.class, int.class, int.class))
                        .or(() -> Utils.reflection()
                                .findConstructor("player_location", type, Player.class, Location.class))
                        .or(() -> Utils.reflection().findConstructor("empty", type))
                        .orElseThrow(() -> new BlockCreationException(player, block, type));

        return switch (constructor.getLabel()) {
            case "player_world" -> constructor.invoke(player, block);
            case "player_unpacked_block" ->
                    constructor.invoke(player, block.getWorld(), block.getX(), block.getY(), block.getZ());
            case "player_raw_unpacked_block" ->
                    constructor.invoke(player, block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
            case "player_location" -> constructor.invoke(player, block.getLocation());
            case "empty" -> constructor.invoke();
            default -> throw new BlockCreationException(player, block, type);
        };
    }


    /**
     * Get all the registered blocks
     *
     * @return the blocks in the registry
     */
    @Override
    public @NotNull @Unmodifiable Collection<Class<? extends EvolvedBlock>> getRegisteredBlocks() {
        return this.blockMap.getValues();
    }

    /**
     * Get a block by its name
     *
     * @param key the block name
     * @return the block
     */
    @Override
    public @NotNull Optional<Class<? extends EvolvedBlock>> getBlock(final @NotNull NamespacedKey key) {
        return this.blockMap.getValue(key);
    }

    @NotNull
    private NamespacedKey generateBlockKey(final @NotNull Plugin plugin, final @NotNull Class<? extends EvolvedBlock> block) {
        String key = String.format("%s:%s", plugin.getName().toLowerCase(), Utils.string().toSnakeCase(block.getSimpleName()));
        NamespacedKey nsk = NamespacedKey.fromString(key);
        if (nsk == null) {
            throw new IllegalArgumentException("Invalid NamespacedKey format: " + key);
        }

        if (this.blockMap.hasKey(nsk)) {
            nsk = NamespacedKey.fromString(String.format("%s_%s", nsk.getKey(), Utils.string().getRandomString()));
        }
        if (nsk == null || this.blockMap.hasKey(nsk)) {
            throw new IllegalArgumentException("Failed to generate a valid NamespacedKey for block: " + block.getSimpleName());
        }

        return nsk;
    }
}
