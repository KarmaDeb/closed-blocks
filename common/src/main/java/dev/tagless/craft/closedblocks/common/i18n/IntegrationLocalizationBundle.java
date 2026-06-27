package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.bundle.locale.Localization;
import dev.tagless.craft.closedblocks.api.bundle.locale.LocalizationBundle;
import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.resource.ResourceFile;
import dev.tagless.craft.closedblocks.api.extension.resource.ResourceItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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
        Path outDef = out.resolve(".lvs");
        if (!Files.exists(out)) {
            Files.createDirectories(out);
        }
        if (!Files.exists(outDef)) {
            Files.createDirectories(outDef);
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Files.setAttribute(outDef, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        }

        for (ResourceFile file : items) {
            Path filePath = out.resolve(file.getName());
            Path originalPath = outDef.resolve(file.getName());

            PropertyResourceBundle bundle;
            if (Files.exists(filePath) && Files.exists(originalPath)) {
                bundle = this.updateExisting(file, originalPath, filePath);
            } else {
                file.export(filePath, ResourceItem.WriteStrategy.REMOVE);
                file.export(originalPath, ResourceItem.WriteStrategy.REMOVE);

                bundle = this.createBundle(filePath);
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
                bundleMap.put(outLocale, new IntegrationLocalization(this.integration, outLocale, bundle));
            } else {
                this.defaultBundle = new IntegrationLocalization(this.integration, Locale.getDefault(), bundle);
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

    private PropertyResourceBundle createBundle(final Path forFile) throws IOException {
        try (InputStream stream = Files.newInputStream(forFile)) {
            return new PropertyResourceBundle(stream);
        }
    }

    private PropertyResourceBundle updateExisting(final ResourceFile file,
                                                  final Path originalPath,
                                                  final Path filePath) throws IOException {
        LanguageVersionSystem strategy = new LanguageVersionSystem(file, originalPath, filePath);
        Map<String, LanguageVersionSystem.I18nEntry> updateKeys = strategy.mergeResult();

        if (updateKeys.isEmpty()) {
            return createBundle(filePath);
        }

        int updates = updateKeys.size();
        this.integration.logger().warn("Updating {} entries of i18n for integration {} ({})",
                updates,
                this.integration.getName(),
                filePath.getFileName());

        Properties exported = new Properties();
        try (InputStream fileData = Files.newInputStream(filePath)) {
            exported.load(fileData);
        }

        for (Map.Entry<String, LanguageVersionSystem.I18nEntry> entry : updateKeys.entrySet()) {
            String key = entry.getKey();
            LanguageVersionSystem.I18nEntry meta = entry.getValue();

            String value = meta.key();
            boolean modified = meta.modified();

            if (value == null) {
                exported.remove(key);
                continue;
            }

            exported.setProperty(key, value);
            if (modified) {
                this.integration.logger().warn("Updating manually modified {} of i18n for integration {} ({})",
                        key,
                        this.integration.getName(),
                        filePath.getFileName());
            }
        }

        try (OutputStream o = Files.newOutputStream(filePath)) {
            exported.store(o, "");
        }
        file.export(originalPath, ResourceItem.WriteStrategy.REMOVE);

        return createBundle(filePath);
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
