package dev.tagless.craft.closedblocks.api.extension.resource;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.function.Supplier;

/**
 * A resource file represents a single file in
 * a {@link ResourceDirectory}
 */
public class ResourceFile implements ResourceItem {

    private final String pathName;
    private final String name;
    private final Supplier<InputStream> data;

    /**
     * Create a new resource file
     * @param pathName the file complete path name
     * @param name the file name
     * @param data the file data supplier
     */
    public ResourceFile(final String pathName, final String name, final Supplier<InputStream> data) {
        this.pathName = pathName;
        this.name = name;
        this.data = data;
    }

    /**
     * Get the item full name
     *
     * @return the item full name
     */
    @Override
    public @NotNull String getPathName() {
        return this.pathName;
    }

    /**
     * Get the item name
     *
     * @return the item name
     */
    @Override
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Get the item data
     *
     * @return the item data
     */
    @NotNull
    public InputStream getData() {
        return this.data.get();
    }
}
