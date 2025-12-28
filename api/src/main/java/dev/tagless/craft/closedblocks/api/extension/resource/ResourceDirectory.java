package dev.tagless.craft.closedblocks.api.extension.resource;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * A resource directory stands for a directory inside
 * a .jar file. This class allows to navigate through the
 * structure of files in the resource
 */
@SuppressWarnings("unused")
public final class ResourceDirectory implements ResourceItem {

    public static final String DIRECTORY_NAME_ROOT = "";

    private final String pathName;
    private final String name;
    private final Map<String, ResourceItem> items;

    /**
     * Create a new resource directory
     * @param pathName the directory full name
     * @param name the directory name
     * @param items the directory items
     */
    private ResourceDirectory(final String pathName, final String name, final Map<String, ResourceItem> items) {
        this.pathName = pathName;
        this.name = name;
        this.items = items;
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
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Get if the item is a directory
     *
     * @return if the item is a directory
     */
    @Override
    public boolean isDirectory() {
        return true;
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
        Path exportTo = destination.toAbsolutePath().resolve(this.name);
        EnumSet<WriteStrategy> s = EnumSet.noneOf(WriteStrategy.class);
        s.addAll(Arrays.asList(strategies));

        if (!Files.exists(exportTo) && s.contains(WriteStrategy.FAIL)) {
            throw new IOException(String.format("Cannot export directory %s to %s. (Directory do not exists)",
                    this.name, exportTo.getParent()));
        }

        if (Files.exists(exportTo) && !Files.isDirectory(exportTo) && s.contains(WriteStrategy.FAIL)) {
            throw new IOException(String.format("Cannot export directory %s to %s. (Exists as file)",
                    this.name, exportTo.getParent()));
        }

        Path tmpOld = null;

        if (Files.exists(exportTo) && !Files.isDirectory(exportTo) && s.contains(WriteStrategy.REMOVE)) {
            tmpOld = Files.createTempFile("rd-tmp", exportTo.getFileName().toString());
        } else if (Files.exists(exportTo) && Files.isDirectory(exportTo) && s.contains(WriteStrategy.REMOVE)) {
            tmpOld = Files.createTempDirectory(String.format("rd-tmp%s", exportTo.getFileName()));
        }

        if (tmpOld != null) {
            Files.move(exportTo, tmpOld, StandardCopyOption.COPY_ATTRIBUTES);
            tmpOld.toFile().deleteOnExit();
        }

        if (!Files.exists(exportTo)) {
            Files.createDirectories(exportTo);
        }

        Set<Path> createdFiles = new LinkedHashSet<>();
        IOException error = null;
        for (ResourceItem item : this.items.values()) {
            try {
                Collection<Path> c = item.export(exportTo, strategies);
                createdFiles.addAll(c);
            } catch (IOException ex) {
                error = ex;
                break;
            }
        }

        if (error != null) {
            createdFiles.forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {}
            });
            Files.delete(exportTo);
            if (tmpOld != null) {
                Files.move(tmpOld, exportTo);
            }

            throw error;
        }

        return Collections.unmodifiableSet(createdFiles);
    }

    /**
     * Get all the files in the current
     * directory
     *
     * @return the directory files
     */
    public Set<String> getFiles() {
        return this.items.keySet().stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * List all the files in the current
     * directory
     *
     * @return the directory files
     */
    public Set<ResourceItem> listFiles() {
        return this.items.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get if the directory is empty
     *
     * @return if the directory is empty
     */
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    /**
     * Get a file in the current resource
     * @param name the resource name
     * @return the resource
     */
    public Optional<ResourceFile> getFile(final String name) {
        if (name.contains("/")) {
            String[] paths = name.split("/");
            String[] dir = Arrays.copyOf(paths, paths.length - 1);
            String flName = paths[paths.length - 1];

            return this.navigate(dir)
                    .flatMap(d -> d.getFile(flName));
        }

        ResourceItem item = this.items.get(name);
        if (!(item instanceof ResourceFile file)) {
            return Optional.empty();
        }

        return Optional.of(file);
    }

    /**
     * Get a file in the current resource
     * @param name the resource name
     * @return the resource
     */
    public Optional<ResourceDirectory> getDirectory(final String name) {
        if (name.contains("/")) {
            String[] paths = name.split("/");

            return this.navigate(paths);
        }

        ResourceItem item = this.items.get(name);
        if (!(item instanceof ResourceDirectory directory)) {
            return Optional.empty();
        }

        return Optional.of(directory);
    }

    /**
     * Get all the files recursively which matches the glob
     * pattern
     *
     * @param pattern the glob pattern
     * @return the files matching
     */
    public Set<ResourceItem> glob(final String pattern) {
        Set<ResourceItem> matched = new LinkedHashSet<>();

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(String.format("glob:%s", pattern));
        for (ResourceItem item : this.items.values()) {
            Path p = Paths.get(item.getPathName());
            if (matcher.matches(p)) {
                matched.add(item);
            }

            if (item.isDirectory()) {
                ResourceDirectory dir = (ResourceDirectory) item;
                matched.addAll(dir.glob(pattern));
            }
        }

        return Collections.unmodifiableSet(matched);
    }

    private Optional<ResourceDirectory> navigate(final String[] paths) {
        ResourceDirectory state = this;
        for (String str : paths) {
            if (str.isBlank()) {
                continue;
            }

            state = state.getDirectory(str).orElse(null);
            if (state == null) {
                return Optional.empty();
            }
        }

        return Optional.of(state);
    }

    public static ResourceDirectory load(final File file) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();

            ResourceScanner scanner = new ResourceScanner(jar, entries);
            Map<String, ResourceItem> items = scanner.scan();

            return new ResourceDirectory(DIRECTORY_NAME_ROOT, DIRECTORY_NAME_ROOT, items);
        }
    }

    private record ResourceScanner(JarFile file, Enumeration<JarEntry> entries) {

        @SuppressWarnings("UnstableApiUsage")
        private Map<String, ResourceItem> scan() throws IOException {
            TempDirectory root = new TempDirectory(DIRECTORY_NAME_ROOT, DIRECTORY_NAME_ROOT);

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (entry.isDirectory() || name.endsWith(".class") || name.endsWith(".java")) {
                    continue;
                }

                if (name.contains("/")) {
                    String[] dir = name.split("/");
                    String[] dirPath = Arrays.copyOf(dir, dir.length - 1);
                    String flName = dir[dir.length - 1];

                    TempDirectory sub = root;
                    for (String str : dirPath) {
                        sub = root.mapDirectory(str);
                    }

                    try (InputStream stream = this.file.getInputStream(entry);
                         FileBackedOutputStream fbos = new FileBackedOutputStream(1024 * 1024)) {

                        ByteStreams.copy(stream, fbos);

                        ResourceFile f = new ResourceFile(entry.getName(), flName, () -> {
                            try {
                                return fbos.asByteSource().openStream();
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                        });
                        sub.appendFile(flName, f);
                    }
                }
            }

            return root.compose();
        }
    }

    private static class TempDirectory {

        private final String pathName;
        private final String name;
        private final Map<String, Object> fileMap = new LinkedHashMap<>();

        private TempDirectory(final String pathName, final String name) {
            this.pathName = pathName;
            this.name = name;
        }

        private void appendFile(final String name, final ResourceFile file) {
            this.fileMap.put(name, file);
        }

        private TempDirectory mapDirectory(final String name) {
            if (this.fileMap.containsKey(name)) {
                return (TempDirectory) this.fileMap.get(name);
            }

            TempDirectory d = new TempDirectory(String.format("%s/%s", this.name, name), name);
            this.fileMap.put(name, d);
            return d;
        }

        private Map<String, ResourceItem> compose() {
            Map<String, ResourceItem> mappedDirectories = new HashMap<>();

            for (Map.Entry<String, Object> o : this.fileMap.entrySet()) {
                String name = o.getKey();
                Object value = o.getValue();

                if (value instanceof ResourceItem) {
                    mappedDirectories.put(name, (ResourceItem) value);
                    continue;
                }

                TempDirectory directory = (TempDirectory) value;
                mappedDirectories.put(name, new ResourceDirectory(this.pathName, name, directory.compose()));
            }

            return mappedDirectories;
        }
    }
}