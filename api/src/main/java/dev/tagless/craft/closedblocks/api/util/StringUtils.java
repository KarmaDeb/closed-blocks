package dev.tagless.craft.closedblocks.api.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String helper class
 */
public final class StringUtils {

    private static final Pattern ACRONYM_PATTERN = Pattern.compile("([A-Z]+)([A-Z][a-z])");
    private static final String[] SUFFIXES = new String[]{
            "holy_cow",
            "bad_apple",
            "liquid",
            "solid",
            "gas",
            "brown_cow",
            "checky_coward",
            "gamer",
            "basic_router",
            "rook",
            "grassy",
            "sunglasses",
            "load_charge",
            "disclaimer",
            "john_freenon",
            "moon_walk",
            "square_bob",
            "flexible_iron",
            "ore_lookup"
    };

    StringUtils() {}

    @NotNull
    public String color(final @UnknownNullability String text, final Object... params) {
        if (text == null || text.trim().isBlank()) {
            return "";
        }

        if (params == null || params.length == 0) {
            return ChatColor.translateAlternateColorCodes('&', text);
        }

        return ChatColor.translateAlternateColorCodes('&', String.format(text, params));
    }

    @NotNull
    public String getRandomString() {
        return SUFFIXES[(int) Math.round(Math.random() * SUFFIXES.length)];
    }

    @NotNull
    public String toSnakeCase(final @UnknownNullability String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        Matcher matcher = ACRONYM_PATTERN.matcher(input);
        String result = matcher.replaceAll("$1_$2");

        result = result.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        result = result.replaceAll("[\\s-.]+", "_");
        result = result.toLowerCase();
        result = result.replaceAll("__+", "_").replaceAll("^_|_$", "");

        return result;
    }
}
