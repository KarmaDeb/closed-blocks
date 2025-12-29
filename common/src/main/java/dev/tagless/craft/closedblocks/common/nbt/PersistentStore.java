package dev.tagless.craft.closedblocks.common.nbt;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class PersistentStore {

    public static PersistentStore forItem(final @NonNull ItemStack stack) {
        return new PersistentItemStore(stack);
    }

    public static PersistentStore forBlock(final @NonNull Block block) {
        return new PersistentBlockStore(block);
    }

    public static PersistentStore forWorld(final @NonNull World world) {
        return new PersistentWorldStore(world);
    }

    public static PersistentStore detect(final @NonNull Object object) {
        return switch (object) {
            case ItemStack stack -> forItem(stack);
            case Block block -> forBlock(block);
            case World world -> forWorld(world);
            default -> throw new IllegalArgumentException("Cannot get persistent store for " + object);
        };
    }

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore setString(final @NotNull String key, final @NotNull String value);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore setInt(final @NotNull String key, final int value);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore setUUID(final @NotNull String key, final @NotNull UUID uuid);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore setBoolean(final @NotNull String key, final boolean value);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore addStringList(final @NotNull String key, final @NotNull String value);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore removeStringList(final @NotNull String key, final @NotNull String value);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore unsetString(final @NotNull String key);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore unsetInt(final @NotNull String key);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore unsetUUID(final @NotNull String key);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore unsetBoolean(final @NotNull String key);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore unsetStringList(final @NotNull String key);

    @NotNull
    public abstract Optional<String> getString(final @NotNull String key);

    @NotNull
    public abstract Optional<Integer> getInteger(final @NotNull String key);

    @NotNull
    public abstract Optional<UUID> getUUID(final @NotNull String key);

    @NotNull
    public abstract Optional<Boolean> getBoolean(final @NotNull String key);

    @NotNull
    public abstract List<String> getStringList(final @NotNull String key);

    @CanIgnoreReturnValue
    @NotNull
    public abstract PersistentStore clear();

    protected final NamespacedKey createKey(final String key) {
        return NamespacedKey.fromString(
                String.format("cbe:%s", key)
        );
    }
}
