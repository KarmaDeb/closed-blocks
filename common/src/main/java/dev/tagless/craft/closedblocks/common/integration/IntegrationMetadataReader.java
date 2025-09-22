package dev.tagless.craft.closedblocks.common.integration;

import dev.tagless.craft.closedblocks.common.integration.exception.PartialIntegrationException;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class IntegrationMetadataReader {

    @NotNull
    public static Optional<IntegrationInfo> readInfo(final @NotNull File file) throws IOException, PartialIntegrationException {
        try (InputStreamReader cbi = readJarFile(file, "cbi.yml"); InputStreamReader plugin = readJarFile(file, "plugin.yml")) {
            if (cbi == null || plugin == null) {
                return Optional.empty();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(cbi);

            if (!data.containsKey("name") || !data.containsKey("class")) {
                return Optional.empty();
            }

            try {
                PluginDescriptionFile description = new PluginDescriptionFile(plugin);

                return Optional.of(
                        new IntegrationInfo(data.get("name").toString(), data.get("class").toString(), description)
                );
            } catch (InvalidDescriptionException ex) {
                throw new PartialIntegrationException(ex, data.get("name").toString());
            }
        }
    }

    @Nullable
    private static InputStreamReader readJarFile(final @NotNull File file, final @NotNull String name) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry(name);
            if (entry == null) {
                return null;
            }

            try (InputStream stream = jar.getInputStream(entry)) {
                return new InputStreamReader(stream, StandardCharsets.UTF_8);
            }
        }
    }
}
