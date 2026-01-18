package dev.tagless.craft.closedblocks.spigot;

import dev.tagless.craft.closedblocks.api.ClosedBlockRegistry;
import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlockRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.engine.Javatype;
import dev.tagless.craft.closedblocks.common.block.BlockRegistry;
import dev.tagless.craft.closedblocks.common.block.behavior.BehaviorRegistry;
import dev.tagless.craft.closedblocks.common.engine.ScriptingEngine;
import dev.tagless.craft.closedblocks.common.integration.IntegrationStateManager;
import dev.tagless.craft.closedblocks.common.integration.manager.IntegrationManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Getter
public class ClosedBlocksSpigotAPI extends ClosedBlocksAPI {

    private final ClosedBlocksSpigotPlugin plugin;
    private final IntegrationStateManager stateManager;
    private final IntegrationManager integrationManager;

    private final ScriptingEngine scriptingEngine = new ScriptingEngine(
            Javatype.pack(ClosedBlocksAPI.class),
            Javatype.pack("net.md_5.bungee."),

            Javatype.instance(Iterator.class),
            Javatype.instance(Collection.class),
            Javatype.instance(Map.class),
            Javatype.instance(CharSequence.class),

            Javatype.type(Character.class),
            Javatype.type(Character.TYPE),
            Javatype.type(Byte.class),
            Javatype.type(Byte.TYPE),
            Javatype.type(Short.class),
            Javatype.type(Short.TYPE),
            Javatype.type(Integer.class),
            Javatype.type(Integer.TYPE),
            Javatype.type(Long.class),
            Javatype.type(Long.TYPE),
            Javatype.type(Float.class),
            Javatype.type(Float.TYPE),
            Javatype.type(Double.class),
            Javatype.type(Double.TYPE),

            Javatype.type(BigInteger.class),
            Javatype.type(BigDecimal.class),

            Javatype.type(UUID.class)
    );

    @Getter(AccessLevel.NONE)
    private final BlockRegistry blockRegistry = new BlockRegistry();
    @Getter(AccessLevel.NONE)
    private final BehaviorRegistry behaviorRegistry = new BehaviorRegistry(blockRegistry);

    public ClosedBlocksSpigotAPI(final @NotNull ClosedBlocksSpigotPlugin plugin) {
        super();
        this.plugin = plugin;

        this.stateManager = new IntegrationStateManager(this.plugin.getLog());
        this.integrationManager = new IntegrationManager(this.stateManager);
    }

    /**
     * Get a plugin registry
     *
     * @param type the registry type
     * @return the plugin registry
     */
    @Override
    public @NotNull <T extends ClosedBlockRegistry> Optional<T> getRegistry(final Class<T> type) {
        T registry = null;
        if (EvolvedBlockRegistry.class.isAssignableFrom(type)) {
            registry = type.cast(this.blockRegistry);
        } else if (EvolvedBlockBehaviorRegistry.class.isAssignableFrom(type)) {
            registry = type.cast(this.behaviorRegistry);
        }

        return Optional.ofNullable(registry);
    }
}