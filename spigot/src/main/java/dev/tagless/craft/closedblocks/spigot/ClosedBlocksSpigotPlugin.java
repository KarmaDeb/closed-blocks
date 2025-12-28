package dev.tagless.craft.closedblocks.spigot;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

@Slf4j
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

    public Logger getLog() {
        return log;
    }
}