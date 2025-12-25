package dev.tagless.craft.closedblocks.common.i18n;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.resource.ResourceFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class LocalizationBundle {

    private final ClosedBlocksIntegration integration;
    private final Map<Locale, IntegrationLocalization> bundleMap = new HashMap<>();

    private IntegrationLocalization defaultBundle;

    public LocalizationBundle(final ClosedBlocksIntegration integration) throws IOException {
        this.integration = integration;
        Set<ResourceFile> localizations = this.integration.getResourceRoot().glob(
                "i18n/messages{_*,}.properties").stream().filter(i -> !i.isDirectory())
                .map(ResourceFile.class::cast)
                .collect(Collectors.toUnmodifiableSet());

        if (localizations.isEmpty()) {
            this.integration.getLogger().log(Level.WARNING, "Cannot load resource bundle from integration {}",
                    this.integration.getName());
            return;
        }

        this.scan(localizations);
    }

    public IntegrationLocalization getLocalization(final Locale locale) {
        IntegrationLocalization i = this.bundleMap.get(locale);
        if (i == null) {
            for (IntegrationLocalization i18n : this.bundleMap.values()) {
                if (i18n.getLocale().getCountry().equals(locale.getCountry())) {
                    i = i18n;
                }

                if (i18n.getLocale().getLanguage().equals(locale.getLanguage())) {
                    return i18n;
                }
            }

            if (i == null) {
                i = this.defaultBundle;
            }
        }

        if (i == null) {
            throw new IllegalStateException(String.format("Integration does not provide localization for %s",
                    locale.toLanguageTag()));
        }

        return i;
    }

    private void scan(final Set<ResourceFile> items) throws IOException {
        for (ResourceFile file : items) {
            PropertyResourceBundle prb = new PropertyResourceBundle(file.getData());
            String name = file.getName().substring(8);
            if (name.startsWith("_")) {
                String[] data = name.substring(1).split("_");
                Locale locale = Locale.of(data[0]);
                if (data.length > 1) {
                    locale = Locale.of(data[0], data[1]);
                }

                bundleMap.put(locale, new IntegrationLocalization(locale, prb));
            } else {
                defaultBundle = new IntegrationLocalization(Locale.getDefault(), prb);
            }
        }

        if (this.defaultBundle == null) {
            this.integration.getLogger().log(Level.WARNING,
                    "Integration {} does not provide a default localization.",
                    this.integration.getName());
        }
    }
}
