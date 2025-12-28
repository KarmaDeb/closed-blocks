package dev.tagless.craft.closedblocks.api.extension.resource;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
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
     * Exports the resource into the specified directory. If the item is
     * a directory, and the provided path is not a directory, the method
     * might remove or fail the file depending on the passed strategies.
     * If the item is a file, and the destination is a file too, the file
     * contents might be replaced, appended or fail depending on strategy.
     * If otherwise, the destination is a directory, the file will be then
     * exported to a file named as {@link #getName()} in that directory, following
     * then the same logic as if destination was a file.
     *
     * @param destination the directory/file to export to
     * @param strategies  the write strategies
     * @return a collection of created files
     * @throws IOException if the export fails
     */
    @Override
    public Collection<Path> export(final @NotNull Path destination, final WriteStrategy... strategies) throws IOException {
        Path exportTo = destination.toAbsolutePath();
        EnumSet<WriteStrategy> s = EnumSet.noneOf(WriteStrategy.class);
        s.addAll(Arrays.asList(strategies));

        if (!Files.exists(exportTo.getParent()) && s.contains(WriteStrategy.FAIL)) {
            throw new IOException(String.format("Cannot export file %s to %s. (Directory do not exists)",
                    this.name, exportTo.getParent()));
        }

        if (Files.exists(exportTo) && Files.isDirectory(exportTo)) {
            exportTo = exportTo.resolve(this.name);
        }
        if (!Files.exists(exportTo.getParent())) {
            Files.createDirectories(exportTo.getParent());
        }
        if (Files.exists(exportTo) && s.contains(WriteStrategy.FAIL)) {
            throw new IOException(String.format("Cannot export file %s to %s. (File already exists)",
                    this.name, exportTo.getParent()));
        }
        if (Files.exists(exportTo) && s.contains(WriteStrategy.REMOVE)) {
            Files.delete(exportTo);
        }

        EnumSet<StandardOpenOption> options = EnumSet.of(StandardOpenOption.CREATE);
        if (s.contains(WriteStrategy.APPEND)) {
            options.add(StandardOpenOption.APPEND);
        }
        if (s.contains(WriteStrategy.TRUNCATE)) {
            options.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        try (InputStream in = this.getData();
             OutputStream out = Files.newOutputStream(exportTo, options.toArray(new StandardOpenOption[0]))) {
            in.transferTo(out);
        }

        return Collections.singleton(exportTo);
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
