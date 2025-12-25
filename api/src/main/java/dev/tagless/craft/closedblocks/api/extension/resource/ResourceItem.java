package dev.tagless.craft.closedblocks.api.extension.resource;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a resource item
 */
public interface ResourceItem {

    /**
     * Get the item full name
     * @return the item full name
     */
    @NotNull
    String getPathName();

    /**
     * Get the item name
     * @return the item name
     */
    @NotNull
    String getName();

    /**
     * Get if the item is a directory
     * @return if the item is a directory
     */
    default boolean isDirectory() {
        return false;
    }
}
