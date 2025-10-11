package dev.tagless.craft.closedblocks.api.material;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The material lookup service allows external plugins to
 * register a material lookup to allow custom blocks to work
 * with ClosedBlocks. By default, the plugin will
 * recognize only vanilla blocks.
 * <pre>
 *     {@code
 *      MaterialLookupService service = MaterialLookupService.getInstance();
 *      if (service.getMaterial("itemsadder:red_block").isPresent()) {
 *          Bukkit.getLogger().info("ItemsAdder is present");
 *      }
 *     }
 * </pre>
 * It's important to note that, implementation of {@link Predicate#test(Object) test} method should
 * return true ONLY if the plugin which registered the current service is the one who provides the
 * material which matches the NamespacedKey passed through the test parameter. This is what the root
 * lookup service uses to correctly map to {@link #getMaterial(NamespacedKey)} and it's required in
 * order to prevent bugs.
 */
public interface MaterialLookupService extends Predicate<NamespacedKey> {

    /**
     * Get the material lookup service
     * @return the material lookup service
     */
    @NotNull
    static MaterialLookupService getInstance() {
        return Objects.requireNonNull(
                Bukkit.getServicesManager().load(MaterialLookupService.class),
                "MaterialLookupService could not be loaded"
        );
    }

    /**
     * Get a material. The ClosedBlocks proxy
     * instance is returned in the case of vanilla material. But if
     * no match is found, an empty optional will be
     * returned instead. This method can also be used to
     * determine if a material exists or is a valid
     * material (using {@link Optional#isEmpty()} or {@link Optional#isPresent()})
     * @param key the material namespace
     * @return the material proxy
     */
    Optional<MaterialProxy> getMaterial(final @NotNull NamespacedKey key);
}