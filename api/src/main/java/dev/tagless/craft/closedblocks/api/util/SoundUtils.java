package dev.tagless.craft.closedblocks.api.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Sound helper class
 */
public final class SoundUtils {

    SoundUtils() {}

    private final Server BUKKIT = Bukkit.getServer();
    private final Registry<Sound> SOUND_REGISTRY = BUKKIT.getRegistry(Sound.class);

    public void emit(final @NotNull Player player, final @UnknownNullability NamespacedKey name) {
        emit(player, name, 1f, 1f);
    }

    public void emit(final @NotNull Player player, final @UnknownNullability NamespacedKey name, final float pitch, final float volume) {
        if (name == null || SOUND_REGISTRY == null) {
            return;
        }

        Sound sound = SOUND_REGISTRY.get(name);
        if (sound == null) {
            return;
        }

        player.stopSound(sound);
        player.playSound(player, sound, pitch, volume);
    }
}
