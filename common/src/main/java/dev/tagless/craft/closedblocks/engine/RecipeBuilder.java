package dev.tagless.craft.closedblocks.engine;

import dev.tagless.craft.closedblocks.api.material.MaterialLookupService;
import dev.tagless.craft.closedblocks.api.material.MaterialProxy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused") @ApiStatus.Experimental
public final class RecipeBuilder {

    public static void register(final String name,
                                final String[][] recipe,
                                final ItemStack result) {
        RecipeNormalizer normalizer = RecipeBuilder.normalize(Objects.requireNonNull(recipe));
        NamespacedKey key = Objects.requireNonNull(NamespacedKey.fromString(String.format("%s:%s", "closedblocks", name)));

        String[] rs = normalizer.result();
        ShapedRecipe newRecipe = new ShapedRecipe(key, Objects.requireNonNull(result))
                .shape(rs);

        Set<Character> processed = new HashSet<>();
        for (String r : rs) {
            char[] chars = r.toCharArray();
            for (char character : chars) {
                if (processed.contains(character)) {
                    continue;
                }

                String resolved = normalizer.resolve(character);
                MaterialProxy proxy = MaterialLookupService
                        .getInstance()
                        .getMaterial(Objects.requireNonNull(NamespacedKey.fromString(resolved)))
                        .orElseThrow();

                processed.add(character);
                newRecipe.setIngredient(character, proxy.asMaterial());
            }
        }

        Bukkit.addRecipe(newRecipe);
    }

    private static RecipeNormalizer normalize(final String[][] recipe) {
        if (recipe.length != 3) {
            throw new IllegalArgumentException("Invalid recipe shape, expected 3 elements but got " + recipe.length);
        }

        RecipeNormalizer normalizer = new RecipeNormalizer(recipe);
        normalizer.normalize();

        return normalizer;
    }
}
