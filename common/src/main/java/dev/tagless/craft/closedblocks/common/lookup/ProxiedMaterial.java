package dev.tagless.craft.closedblocks.common.lookup;

import dev.tagless.craft.closedblocks.api.material.MaterialProxy;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ProxiedMaterial implements MaterialProxy {

    private final Plugin plugin;
    private final Material material;

    public ProxiedMaterial(final Plugin plugin, final Material material) {
        this.plugin = plugin;
        this.material = material;
    }
    /**
     * Get the material proxy provider
     *
     * @return the provider of the proxy
     */
    @Override
    public @NotNull Plugin getProvider() {
        return this.plugin;
    }

    /**
     * Get if a material is a block
     *
     * @return if the material is a block
     */
    @Override
    public boolean isBlock() {
        return this.material.isBlock();
    }

    /**
     * Get if a material is solid. An example
     * of a non-solid block is water.
     *
     * @return if the material is solid
     */
    @Override
    public boolean isSolid() {
        return this.material.isSolid();
    }

    /**
     * Get if the material is an item
     *
     * @return if the material is an item
     */
    @Override
    public boolean isItem() {
        return this.material.isItem();
    }

    /**
     * Get if the material is air
     *
     * @return if the material is air
     */
    @Override
    public boolean isAir() {
        return this.material.isAir();
    }

    /**
     * Get if the material is flammable
     *
     * @return if the material is flammable
     */
    @Override
    public boolean isFlammable() {
        return this.material.isFlammable();
    }

    /**
     * Get if the material is burnable
     *
     * @return if the material is burnable
     */
    @Override
    public boolean isBurnable() {
        return this.material.isBurnable();
    }

    /**
     * Get if the material is fuel
     *
     * @return if the material is fuel
     */
    @Override
    public boolean isFuel() {
        return this.material.isFuel();
    }

    /**
     * Get if the material is occluding
     *
     * @return if the material is occluding
     */
    @Override
    public boolean isOccluding() {
        return this.material.isOccluding();
    }

    /**
     * Get if gravity affects the material
     *
     * @return if gravity affects the material
     */
    @Override
    public boolean hasGravity() {
        return this.material.hasGravity();
    }

    /**
     * Get if the material is interactable
     *
     * @return if the material is interactable
     */
    @Override
    public boolean isInteractable() {
        return this.material.isInteractable();
    }

    /**
     * Get if the material is edible
     *
     * @return if the material is edible
     */
    @Override
    public boolean isEdible() {
        return this.material.isEdible();
    }

    /**
     * Generate an item stack from the
     * material
     *
     * @return the item stack
     */
    @Override
    public @NotNull Optional<ItemStack> generateItem() {
        return Optional.of(new ItemStack(this.material));
    }

    /**
     * Generate a block from the
     * material
     *
     * @param world the world in where to place the block
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the block
     */
    @Override
    public @NotNull Optional<Block> generateBlock(final @NotNull World world, final int x, final int y, final int z) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(this.material);

        return Optional.of(block);
    }
}