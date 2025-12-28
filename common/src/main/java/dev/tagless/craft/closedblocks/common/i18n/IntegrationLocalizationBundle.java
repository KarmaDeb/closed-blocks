package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.bundle.locale.Localization;
import dev.tagless.craft.closedblocks.api.bundle.locale.LocalizationBundle;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.resource.ResourceFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class IntegrationLocalizationBundle implements LocalizationBundle {

    @NotNull
    private ClosedBlocksIntegration integration;
    private final Map<Locale, IntegrationLocalization> bundleMap = new HashMap<>();

    private IntegrationLocalization defaultBundle;

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    public IntegrationLocalizationBundle(final @NotNull ClosedBlocksIntegration integration) throws IOException {
        this.integration = integration;
        if (integration != null) {
            Set<ResourceFile> localizations = this.integration.getResourceRoot()
                    .glob("i18n/messages{_*,}.properties")
                    .stream()
                    .filter(i -> !i.isDirectory())
                    .map(ResourceFile.class::cast)
                    .collect(Collectors.toUnmodifiableSet());

            if (localizations.isEmpty()) {
                this.integration.logger().info("Integration {} does not provide any localization",
                        this.integration.getName());
                return;
            }

            this.scan(localizations);
        }
    }

    /**
     * Get a locale of the bundle
     *
     * @param locale the locale to get for
     * @return the bundle locale
     * @throws IllegalStateException if there's no localization for the
     * provided locale
     */
    @Override
    public @NotNull Localization get(final @Nullable Locale locale) throws IllegalStateException {
        IntegrationLocalization i = this.defaultBundle;
        if (locale != null) {
            for (IntegrationLocalization i18n : this.bundleMap.values()) {
                if (i18n.locale().getCountry().equals(locale.getCountry())) {
                    i = i18n;
                }

                if (i18n.locale().getLanguage().equals(locale.getLanguage())) {
                    return i18n;
                }
            }
        }

        if (i == null) {
            throw new IllegalStateException(String.format("Integration does not provide localization for %s",
                    locale == null ? "default" : locale.toLanguageTag()));
        }

        return i;
    }

    private void scan(final Set<ResourceFile> items) throws IOException {
        Path out = this.integration.getDataFolder().toPath().resolve("i18n");
        if (!Files.exists(out)) {
            Files.createDirectories(out);
        }

        for (ResourceFile file : items) {
            Path filePath = out.resolve(file.getName());

            Properties properties = new Properties();
            PropertyResourceBundle prb;
            if (!Files.exists(filePath)) {
                file.export(out);
                prb = new PropertyResourceBundle(file.getData());
                for (String k : prb.keySet()) {
                    properties.put(k, prb.getObject(k));
                }
            } else {
                properties.load(file.getData());
                try (InputStream in = Files.newInputStream(filePath)) {
                    prb = new PropertyResourceBundle(in);
                }
            }

            if (!prb.containsKey("version") && !properties.containsKey("version")) {
                this.integration.logger().warn("Missing version property in i18n for integration {} ({})",
                        this.integration.getName(),
                        filePath.getFileName());
            } else {
                String exported = null;
                String internal = properties.getProperty("version");
                try {
                    exported = prb.getString("version");
                } catch (MissingResourceException ignored) {}

                if (!internal.equals(exported)) {
                    this.integration.logger().info("Updating localization {} of {} from {} to {}",
                            filePath.getFileName(),
                            this.integration.getName(),
                            exported == null ? "0" : exported,
                            internal);

                    for (String key : prb.keySet()) {
                        if (key.equals("version")) {
                            continue;
                        }

                        properties.setProperty(key, prb.getString(key));
                    }

                    try (OutputStream o = Files.newOutputStream(filePath, StandardOpenOption.CREATE)) {
                        properties.store(o, "");
                    }
                }
            }

            String name = file.getName().substring(8).replaceAll("\\..*$", "");
            if (name.startsWith("_")) {
                String[] data = name.substring(1).split("_");
                Locale outLocale = Locale.of(data[0]);
                if (data.length > 1) {
                    outLocale = Locale.of(data[0], data[1]);
                }

                this.integration.logger().info("Loaded localization for {} from {}",
                        outLocale, this.integration.getName());
                bundleMap.put(outLocale, new IntegrationLocalization(this.integration, outLocale, prb));
            } else {
                defaultBundle = new IntegrationLocalization(this.integration, Locale.getDefault(), prb);
            }
        }

        if (this.defaultBundle == null) {
            this.integration.logger().warn("Integration {} does not provide a default localization.",
                    this.integration.getName());

            try (InputStream emptyStream = new ByteArrayInputStream(new byte[0])) {
                this.defaultBundle = new IntegrationLocalization(this.integration, Locale.getDefault(),
                        new PropertyResourceBundle(emptyStream));
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static IntegrationLocalizationBundle empty(final ClosedBlocksIntegration integration) {
        try(InputStream emptyStream = new ByteArrayInputStream(new byte[0])) {
            IntegrationLocalizationBundle b = new IntegrationLocalizationBundle(null);
            b.integration = integration;
            b.defaultBundle = new IntegrationLocalization(integration, Locale.getDefault(),
                    new PropertyResourceBundle(emptyStream));

            return b;
        } catch (IOException ignored) {}

        return null; //never
    }
}
