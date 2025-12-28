package dev.tagless.craft.closedblocks.api.extension;

import dev.tagless.craft.closedblocks.api.annotation.Doppelganger;
import dev.tagless.craft.closedblocks.api.bundle.locale.LocalizationBundle;
import dev.tagless.craft.closedblocks.api.extension.exception.NotInitializedException;
import dev.tagless.craft.closedblocks.api.extension.feature.FeatureManager;
import dev.tagless.craft.closedblocks.api.extension.resource.ResourceDirectory;
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A closed block integration is NOT a requirement,
 * but it is recommended to allow server owners to
 * completely disable features provided by a plugin
 * which iterates with ClosedBlocks plugin.
 * <p/>
 * WARNING: ClosedBlocksIntegrations are doppelgänger of a plugin.
 * This means the class behaves like your plugin, looks like your plugin, BUT IT'S NOT
 * your plugin. If you see strange behavior, run and hide.
 */
@Doppelganger
public abstract class ClosedBlocksIntegration implements Plugin {

    private static final String FEATURE_FEATURE_MANAGER = "featureManager";
    private static final String FEATURE_PLUGIN = "plugin";
    private static final String FEATURE_RESOURCE = "resourceRoot";

    @SuppressWarnings("FieldMayBeFinal")
    private IntegrationState state = IntegrationState.UNLOADED; //Modified through reflection

    @SuppressWarnings("unused")
    private String name; //Modified through reflection
    @SuppressWarnings("unused")
    private FeatureManager featureManager; //Modified through reflection
    @SuppressWarnings("unused")
    private Plugin plugin; //Modified through reflection
    @SuppressWarnings("unused")
    private LocalizationBundle localization; //Modified through reflection

    private WeakReference<ResourceDirectory> resource;

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
    public void enable(final @NotNull IntegrationStateReason reason) {

    }

    /**
     * Disables the integration
     * @param reason the reason of why the integration was disabled
     */
    public void disable(final @NotNull IntegrationStateReason reason) {

    }

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
     * Gets the current jar file as a resource
     * @return the jar file as a resource
     */
    public final @NotNull ResourceDirectory getResourceRoot() {
        if (this.plugin == null) {
            throw new NotInitializedException(this, ClosedBlocksIntegration.FEATURE_RESOURCE);
        }

        ResourceDirectory current = this.resource.get();
        if (current != null) {
            return current;
        }

        try {
            URI location = this.plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            File file = new File(location);

            ResourceDirectory dir = ResourceDirectory.load(file);
            this.resource = new WeakReference<>(dir);

            return dir;
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
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
    public final @NotNull Logger getLogger() {
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
    public final @Nullable List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
                                                final @NotNull String label, final @NotNull String[] args) {
        return this.plugin.onTabComplete(sender, command, label, args);
    }

    /**
     * Get the integration's localization
     * @return the localization of the integration
     */
    @NotNull @SuppressWarnings("unused")
    public LocalizationBundle getBundle() {
        return this.localization;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     *
     * <p>
     * An equivalence relation partitions the elements it operates on
     * into <i>equivalence classes</i>; all the members of an
     * equivalence class are equal to each other. Members of an
     * equivalence class are substitutable for each other, at least
     * for some purposes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @implSpec The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * In other words, under the reference equality equivalence
     * relation, each equivalence class only has a single element.
     * @apiNote It is generally necessary to override the {@link #hashCode() hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Plugin pl)) {
            return false;
        }

        return pl.equals(this.plugin);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@link
     *     #equals(Object) equals} method, then calling the {@code
     *     hashCode} method on each of the two objects must produce the
     *     same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link #equals(Object) equals} method, then
     *     calling the {@code hashCode} method on each of the two objects
     *     must produce distinct integer results.  However, the programmer
     *     should be aware that producing distinct integer results for
     *     unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @return a hash code value for this object.
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public final int hashCode() {
        if (this.plugin == null) {
            return super.hashCode();
        }

        return this.plugin.hashCode();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public final String toString() {
        if (this.plugin == null) {
            return super.toString();
        }

        return this.plugin.toString();
    }
}