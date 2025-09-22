package dev.tagless.craft.closedblocks.api.extension;

import dev.tagless.craft.closedblocks.api.extension.exception.NotInitializedException;
import dev.tagless.craft.closedblocks.api.extension.feature.ClosedBlocksFeature;
import dev.tagless.craft.closedblocks.api.extension.feature.FeatureManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A closed block integration is NOT a requirement,
 * but it is recommended to allow server owners to
 * completely disable features provided by a plugin
 * which iterates with ClosedBlocks plugin.
 */
public abstract class ClosedBlocksIntegration implements Plugin {

    private static final String FEATURE_FEATURE_MANAGER = "featureManager";
    private static final String FEATURE_PLUGIN = "plugin";

    @SuppressWarnings("FieldMayBeFinal")
    private IntegrationState state = IntegrationState.UNLOADED; //Modified through reflection
    private String name; //Modified through reflection
    private FeatureManager featureManager; //Modified through reflection
    private Plugin plugin; //Modified through reflection

    private final Collection<? extends ClosedBlocksFeature> features = ConcurrentHashMap.newKeySet();

    /**
     * Get the integration name.
     * @return the name of the integration
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * Enables the integration
     * @param reason the reason of why the integration was enabled
     */
    public abstract void enable(final IntegrationStateReason reason);

    /**
     * Disables the integration
     * @param reason the reason of why the integration was disabled
     */
    public abstract void disable(final IntegrationStateReason reason);

    /**
     * Get the integration feature manager
     * @return the feature manager
     * @throws NotInitializedException if the integration has not been initialized
     */
    @NotNull
    public final FeatureManager getFeatureManager() throws NotInitializedException {
        if (this.featureManager == null) {
            throw new NotInitializedException(this, FEATURE_FEATURE_MANAGER);
        }

        return this.featureManager;
    }

    /**
     * Get the integration plugin
     * @return the plugin
     * @throws NotInitializedException if the integration has not been initialized
     */
    @NotNull
    public final Plugin getPlugin() {
        if (this.featureManager == null) {
            throw new NotInitializedException(this, FEATURE_PLUGIN);
        }

        return this.plugin;
    }

    /**
     * Get the current integration state. By
     * default, it will always be {@link IntegrationState#UNLOADED}
     * @return the integration current state
     */
    public final IntegrationState getState() {
        return this.state;
    }

    /**
     * Returns the folder that the plugin data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    @Override
    public final @NotNull File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    /**
     * Returns the plugin.yaml file containing the details for this plugin
     *
     * @return Contents of the plugin.yaml file
     */
    @Override
    public final @NotNull PluginDescriptionFile getDescription() {
        return this.plugin.getDescription();
    }

    /**
     * Gets a {@link FileConfiguration} for this plugin, read through
     * "config.yml"
     * <p>
     * If there is a default config.yml embedded in this plugin, it will be
     * provided as a default for this Configuration.
     *
     * @return Plugin configuration
     */
    @Override
    public final @NotNull FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }

    /**
     * Gets an embedded resource in this plugin
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
    @Override
    public final @Nullable InputStream getResource(final @NotNull String filename) {
        return this.plugin.getResource(filename);
    }

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}.
     */
    @Override
    public final void saveConfig() {
        this.plugin.saveConfig();
    }

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}.
     * <p>
     * This should fail silently if the config.yml already exists.
     */
    @Override
    public final void saveDefaultConfig() {
        this.plugin.saveDefaultConfig();
    }

    /**
     * Saves the raw contents of any resource embedded with a plugin's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the plugin's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *                     plugin's .jar file. (No preceding slash).
     * @param replace      if true, the embedded resource will overwrite the
     *                     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *                                  or points to a nonexistent resource.
     */
    @Override
    public final void saveResource(final @NotNull String resourcePath, final boolean replace) {
        this.plugin.saveResource(resourcePath, replace);
    }

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
    @Override
    public final void reloadConfig() {
        this.plugin.reloadConfig();
    }

    /**
     * Gets the associated PluginLoader responsible for this plugin
     *
     * @return PluginLoader that controls this plugin
     */
    @Override
    public final @NotNull PluginLoader getPluginLoader() {
        return this.plugin.getPluginLoader();
    }

    /**
     * Returns the Server instance currently running this plugin
     *
     * @return Server running this plugin
     */
    @Override
    public final @NotNull Server getServer() {
        return this.plugin.getServer();
    }

    /**
     * Returns a value indicating whether or not this plugin is currently
     * enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
    @Override
    public final boolean isEnabled() {
        return this.plugin != null && this.plugin.isEnabled();
    }

    /**
     * Called when this plugin is disabled
     */
    @Override
    public final void onDisable() {
    }

    /**
     * Called after a plugin is loaded but before it has been enabled.
     * <p>
     * When multiple plugins are loaded, the onLoad() for all plugins is
     * called before any onEnable() is called.
     */
    @Override
    public final void onLoad() {
    }

    /**
     * Called when this plugin is enabled
     */
    @Override
    public final void onEnable() {

    }

    /**
     * Simple boolean if we can still nag to the logs about things
     *
     * @return boolean whether we can nag
     */
    @Override
    public final boolean isNaggable() {
        return this.plugin.isNaggable();
    }

    /**
     * Set naggable state
     *
     * @param canNag is this plugin still naggable?
     */
    @Override
    public final void setNaggable(final boolean canNag) {
        this.plugin.setNaggable(canNag);
    }

    /**
     * Gets a {@link ChunkGenerator} for use in a default world, as specified
     * in the server configuration
     *
     * @param worldName Name of the world that this will be applied to
     * @param id        Unique ID, if any, that was specified to indicate which
     *                  generator was requested
     * @return ChunkGenerator for use in the default world generation
     */
    @Override
    public final @Nullable ChunkGenerator getDefaultWorldGenerator(final @NotNull String worldName, final @Nullable String id) {
        return this.plugin.getDefaultWorldGenerator(worldName, id);
    }

    /**
     * Gets a {@link BiomeProvider} for use in a default world, as specified
     * in the server configuration
     *
     * @param worldName Name of the world that this will be applied to
     * @param id        Unique ID, if any, that was specified to indicate which
     *                  biome provider was requested
     * @return BiomeProvider for use in the default world generation
     */
    @Override
    public final @Nullable BiomeProvider getDefaultBiomeProvider(final @NotNull String worldName, final @Nullable String id) {
        return this.plugin.getDefaultBiomeProvider(worldName, id);
    }

    /**
     * Returns the plugin logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the plugin's
     * name.
     *
     * @return Logger associated with this plugin
     */
    @Override
    public @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public final boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command,
                             final @NotNull String label, final @NotNull String[] args) {
        return this.plugin.onCommand(sender, command, label, args);
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public @Nullable List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
                                                final @NotNull String label, final @NotNull String[] args) {
        return this.plugin.onTabComplete(sender, command, label, args);
    }
}