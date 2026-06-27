package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.extension.resource.ResourceFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RequiredArgsConstructor
public class LanguageVersionSystem {

    private final ResourceFile file;
    private final Path generated;
    private final Path currentFile;

    public final Map<String, I18nEntry> mergeResult() throws IOException {
        Map<String, I18nEntry> requiresUpdate = new HashMap<>();
        try (InputStream internalStream = file.getData();
             InputStream externalStream = Files.newInputStream(this.generated);
             InputStream currentStream = Files.newInputStream(this.currentFile)) {
            Properties internal = new Properties();
            Properties external = new Properties();
            Properties current = new Properties();

            internal.load(internalStream);
            external.load(externalStream);
            current.load(currentStream);

            for (Map.Entry<Object, Object> entry : internal.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String inValue = entry.getValue() != null ? String.valueOf(entry.getValue()) : null;
                String exValue = external.getProperty(key);
                String cuValue = current.getProperty(key);

                if (!external.containsKey(key) || !current.containsKey(key)) {
                    if (inValue == null) {
                        continue;
                    }

                    requiresUpdate.put(key, new I18nEntry(inValue, false));
                    continue;
                }

                if (inValue == null) {
                    if (exValue == null && cuValue == null) {
                        continue;
                    }

                    requiresUpdate.put(key, new I18nEntry(null, cuValue != null));
                    continue;
                }

                if (!inValue.equals(exValue)) {
                    requiresUpdate.put(key, new I18nEntry(inValue, cuValue != null));
                }
            }
        }

        return requiresUpdate;
    }

    public record I18nEntry(String key, boolean modified) {

    }
}
