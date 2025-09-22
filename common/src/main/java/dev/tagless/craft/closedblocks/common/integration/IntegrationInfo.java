package dev.tagless.craft.closedblocks.common.integration;

import org.bukkit.plugin.PluginDescriptionFile;

public record IntegrationInfo(String integrationName, String integrationClass, PluginDescriptionFile plugin) {
}
