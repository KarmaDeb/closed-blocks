package dev.tagless.craft.closedblocks.api.block;

import dev.tagless.craft.closedblocks.api.BlockEvolution;
import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.annotation.FinalDefaultImplementation;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.ProxiedBlockBehavior;
import dev.tagless.craft.closedblocks.api.extension.feature.ClosedBlocksFeature;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;

/**
 * The evolved block interface provides the façade for
 * any other evolved block the plugin provides.
 */
public interface EvolvedBlock extends ClosedBlocksFeature {

    /**
     * Get if the current feature is
     * enabled. By default, it will always be
     * true unless explicitly specified
     *
     * @return if the feature is enabled
     */
    @Override @FinalDefaultImplementation
    default boolean isEnabled() {
        return ClosedBlocksAPI.getInstance()
                .getRegistry(EvolvedBlockRegistry.class)
                .map(registry -> registry.isEnabled(this.getClass()))
                .orElse(false);
    }

    /**
     * Get the block world. This method can
     * return an empty optional if the world is yet
     * not loaded
     * @return the world of the block
     */
    @NotNull
    Optional<World> getWorld();

    /**
     * Get the player who placed the block
     * @return the player who placed the block
     */
    @NotNull
    OfflinePlayer getOwner();

    /**
     * Get the player (online) who placed the
     * block
     * @return the player who placed the block
     */
    @NotNull
    default Optional<Player> getPlayer() {
        return Optional.ofNullable(this.getOwner().getPlayer());
    }

    /**
     * Get the block material. The value returned by
     * this method is a namespaced key to
     * ensure support with any other plugin that
     * extends the vanilla materials introducing
     * new ones.
     * @return the block material
     */
    @NotNull
    NamespacedKey getMaterial();

    /**
     * Set the block material. If the material is anything
     * but a block, the material will be ignored
     * @param material the material to replace with
     */
    void setMaterial(final @NotNull NamespacedKey material);

    /**
     * Get the block X position
     * in the world
     * @return the block X position
     */
    int getX();

    /**
     * Get the block Y position
     * in the world
     * @return the block Y position
     */
    int getY();

    /**
     * Get the block Z position
     * in the world
     * @return the block Z position
     */
    int getZ();

    /**
     * Get the evolved block as a minecraft
     * block
     * @return the evolved block
     */
    @NotNull
    default Optional<Block> getBlock() {
        Optional<World> worldOptional = this.getWorld();
        if (worldOptional.isEmpty()) {
            return Optional.empty();
        }

        World world = worldOptional.get();
        return Optional.of(world.getBlockAt(this.getX(), this.getY(), this.getZ()));
    }

    /**
     * Get the block evolution type
     * @return the block evolution type
     */
    @NotNull
    BlockEvolution getEvolution();

    /**
     * Get a behavior of the block
     * @param behaviorType the behavior type
     * @return the block behavior
     * @param <T> the behavior type
     */
    @NotNull @Unmodifiable
    <T extends BlockBehavior> Optional<T> getBehavior(final @NotNull Class<T> behaviorType);

    /**
     * Get all the behaviors this block
     * provides
     * @return the block behaviors
     */
    @NotNull
    ProxiedBlockBehavior<? extends BlockBehavior> getBehavior();

    /**
     * Removes the block. Unlike the {@link #destroy(Player, ItemStack) destroy} method,
     * this method DOES NOT trigger any event, and does not perform any validation.
     */
    void remove();

    /**
     * Get if the player can destroy the block
     * @param player the player trying to destroy the block
     * @param tool the tool (item) the player is holding. For a generic
     *             "break-permission" check, this should be set to null
     * @return if the player is able to destroy
     * the block
     */
    boolean canDestroy(final @NotNull Player player, final @Nullable ItemStack tool);

    /**
     * Get if the player can destroy the block with
     * a tool of the provided material
     * @param player the player trying to destroy the block
     * @param material the tool (item) material the player could
     *                 have
     * @return if the player is able to destroy
     * the block
     */
    boolean canDestroy(final @NotNull Player player, final @NotNull NamespacedKey material);

    /**
     * Destroys the block. Unlike the {@link #remove() remove} method,
     * this method DOES trigger an event, and performs pre-validations before
     * breaking the block
     * @param player the player trying to destroy the block
     * @param item the item the player is currently holding. If the player
     *             is holding nothing, this can be set to null
     * @return if the block could be destroyed
     */
    boolean destroy(final @Nullable Player player, final @Nullable ItemStack item);

    /**
     * Place the block in the world. This method should be
     * called just once unless {@link #destroy(Player, ItemStack)} has been
     * previously called, and in case the block is already placed,
     * further calls to this method should be completely ignored
     * @param player the player who placed the block
     * @param block the block in the world in where the evolved
     *              block was placed
     */
    void place(final @NotNull Player player, final @NotNull Block block);
}