package dev.tagless.craft.closedblocks.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ClosedBlocksSpigotPlugin extends JavaPlugin {

    private final ClosedBlocksSpigotAPI api = new ClosedBlocksSpigotAPI(this);
    private final ClosedBlocksInit init = new ClosedBlocksInit(api);

    @Override
    public void onEnable() {
        this.init.registerServices();

        // https://www.spigotmc.org/threads/how-to-make-plugin-load-after-all-plugins.284987/#post-2754465
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.api.getPlugin(), () -> {
            this.init.scanIntegrations();
            this.init.loadIntegrations();
        });
    }

    @Override
    public void onDisable() {

    }
}