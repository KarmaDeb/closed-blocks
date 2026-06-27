package dev.tagless.craft.closedblocks.api.material;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A material proxy is a proxy between a
 * "generic" material and the plugin implementing
 * the material. For vanilla materials, the proxy
 * is always the ClosedBlocks plugin.
 */
@SuppressWarnings("unused")
public interface MaterialProxy {

    /**
     * Get the material proxy provider
     * @return the provider of the proxy
     */
    @NotNull
    Plugin getProvider();

    /**
     * Get the proxy as a material. Please note this
     * might not return the custom material, causing the proxied
     * methods to not return the same as the proxy
     * @return the proxy element as a material
     */
    Material asMaterial();

    /**
     * Get if a material is a block
     * @return if the material is a block
     */
    boolean isBlock();

    /**
     * Get if a material is solid. An example
     * of a non-solid block is water.
     * @return if the material is solid
     */
    boolean isSolid();

    /**
     * Get if the material is an item
     * @return if the material is an item
     */
    boolean isItem();

    /**
     * Get if the material is air
     * @return if the material is air
     */
    boolean isAir();

    /**
     * Get if the material is flammable
     * @return if the material is flammable
     */
    boolean isFlammable();

    /**
     * Get if the material is burnable
     * @return if the material is burnable
     */
    boolean isBurnable();

    /**
     * Get if the material is fuel
     * @return if the material is fuel
     */
    boolean isFuel();

    /**
     * Get if the material is occluding
     * @return if the material is occluding
     */
    boolean isOccluding();

    /**
     * Get if gravity affects the material
     * @return if gravity affects the material
     */
    boolean hasGravity();

    /**
     * Get if the material is interactable
     * @return if the material is interactable
     */
    boolean isInteractable();

    /**
     * Get if the material is edible
     * @return if the material is edible
     */
    boolean isEdible();

    /**
     * Generate an item stack from the
     * material
     * @return the item stack
     */
    @NotNull
    Optional<ItemStack> generateItem();

    /**
     * Generate a block from the
     * material
     * @param world the world in where to place the block
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block
     */
    @NotNull
    Optional<Block> generateBlock(final @NotNull World world, final int x, final int y, final int z);
}