package dev.tagless.craft.closedblocks.common.nbt;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PersistentWorldStore extends PersistentStore {

    private final @NonNull World world;

    @Override
    public @NotNull PersistentStore setString(final @NotNull String key, final @NotNull String value) {
        set(createKey(key), PersistentDataType.STRING, value);
        return this;
    }

    @Override
    public @NotNull PersistentStore setInt(final @NotNull String key, final int value) {
        set(createKey(key), PersistentDataType.INTEGER, value);
        return this;
    }

    @Override
    public @NotNull PersistentStore setUUID(final @NotNull String key, final @NotNull UUID uuid) {
        set(createKey(key), PersistentDataType.STRING, uuid.toString());
        return this;
    }

    @Override
    public @NotNull PersistentStore setBoolean(final @NotNull String key, final boolean value) {
        set(createKey(key), PersistentDataType.BOOLEAN, value);
        return this;
    }

    @Override
    public @NotNull PersistentStore addStringList(final @NotNull String key, final @NotNull String value) {
        List<String> current = this.getStringList(key);
        if (!current.contains(value)) {
            current.add(value);
        }

        set(createKey(key), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), current);
        return this;
    }

    @Override
    public @NotNull PersistentStore removeStringList(final @NotNull String key, final @NotNull String value) {
        List<String> current = this.getStringList(key);
        current.remove(value);

        set(createKey(key), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), current);
        return this;
    }

    @Override
    public @NotNull PersistentStore unsetString(final @NotNull String key) {
        unset(createKey(key), PersistentDataType.STRING);
        return this;
    }

    @Override
    public @NotNull PersistentStore unsetInt(final @NotNull String key) {
        unset(createKey(key), PersistentDataType.INTEGER);
        return this;
    }

    @Override
    public @NotNull PersistentStore unsetUUID(final @NotNull String key) {
        unset(createKey(key), PersistentDataType.STRING);
        return this;
    }

    @Override
    public @NotNull PersistentStore unsetBoolean(final @NotNull String key) {
        unset(createKey(key), PersistentDataType.BOOLEAN);
        return this;
    }

    @Override
    public @NotNull PersistentStore unsetStringList(final @NotNull String key) {
        unset(createKey(key), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING));
        return this;
    }

    @Override
    public @NotNull Optional<String> getString(final @NotNull String key) {
        return get(createKey(key), PersistentDataType.STRING);
    }

    @Override
    public @NotNull Optional<Integer> getInteger(final @NotNull String key) {
        return get(createKey(key), PersistentDataType.INTEGER);
    }

    @Override
    public @NotNull Optional<UUID> getUUID(final @NotNull String key) {
        return get(createKey(key), PersistentDataType.STRING).map(r -> {
            try {
                return UUID.fromString(r);
            } catch (IllegalArgumentException ignored) {}

            return null;
        });
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(final @NotNull String key) {
        return get(createKey(key), PersistentDataType.BOOLEAN);
    }

    @Override
    public @NotNull List<String> getStringList(final @NotNull String key) {
        return get(createKey(key), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING))
                .orElse(Collections.emptyList());
    }

    @Override
    public @NotNull PersistentStore clear() {
        PersistentDataContainer container = this.world.getPersistentDataContainer();
        container.getKeys().removeIf(
                key -> key.getNamespace().equals("cbe"));

        return this;
    }

    private <A, B> void set(final NamespacedKey key, final PersistentDataType<A, B> type, final B value) {
        PersistentDataContainer container = this.world.getPersistentDataContainer();
        container.set(key, type, value);
    }

    private <A, B> void unset(final NamespacedKey key, final PersistentDataType<A, B> type) {
        PersistentDataContainer container = this.world.getPersistentDataContainer();
        if (container.has(key, type)) {
            container.remove(key);
        }
    }

    private <A, B> Optional<B> get(final NamespacedKey key, final PersistentDataType<A, B> type) {
        PersistentDataContainer container = this.world.getPersistentDataContainer();
        if (container.has(key, type)) {
            return Optional.ofNullable(container.get(key, type));
        }

        return Optional.empty();
    }
}
