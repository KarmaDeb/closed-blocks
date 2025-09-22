package dev.tagless.craft.closedblocks.api;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The BlockEvolution enum contains all the evolutions
 * of vanilla blocks that the plugin provides.
 */
public enum BlockEvolution {
    ELEVATOR("closed:elevator");

    private final String namespace;

    BlockEvolution(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get the evolution block namespace
     * @return the namespace
     */
    @NotNull
    public NamespacedKey getNamespace() {
        return Objects.requireNonNull(NamespacedKey.fromString(this.namespace));
    }
}
