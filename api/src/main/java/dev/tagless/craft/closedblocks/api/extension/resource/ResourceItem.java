package dev.tagless.craft.closedblocks.api.extension.resource;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

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

    /**
     * Exports the resource into the specified directory. If the item is
     * a directory, and the provided path is not a directory, the method
     * might remove or fail the file depending on the passed strategies.
     * If the item is a file, and the destination is a file too, the file
     * contents might be replaced, appended or fail depending on strategy.
     * If otherwise, the destination is a directory, the file will be then
     * exported to a file named as {@link #getName()} in that directory, following
     * then the same logic as if destination was a file.
     * @param destination the directory/file to export to
     * @param strategies the write strategies
     * @return a collection of created files
     * @throws IOException if the export fails
     */
    Collection<Path> export(final @NotNull Path destination, final WriteStrategy... strategies) throws IOException;

    /**
     * File write strategy when exporting
     */
    enum WriteStrategy {
        /**
         * Append contents to the existing
         * data
         */
        APPEND,
        /**
         * Truncate the existing content
         */
        TRUNCATE,
        /**
         * Fail the operation
         */
        FAIL,
        /**
         * Remove the file and re-create (similar to replace, but
         * ensure the file gets removed first, removing any kind of
         * meta-data)
         */
        REMOVE
    }
}
