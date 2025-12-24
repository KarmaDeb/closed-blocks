package dev.tagless.craft.closedblocks.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class ClosedBlocksSpigotPlugin extends JavaPlugin {

    private final ClosedBlocksSpigotAPI api = new ClosedBlocksSpigotAPI(this);
    private final ClosedBlocksInit init = new ClosedBlocksInit(api);

    @Override
    public void onLoad() {
        this.init.scanIntegrations();
    }

    @Override
    public void onEnable() {
        this.init.registerServices();
        this.init.loadIntegrations();
    }

    @Override
    public void onDisable() {

    }
}