package dev.tagless.craft.closedblocks.common.lookup;

import dev.tagless.craft.closedblocks.api.material.MaterialLookupService;
import dev.tagless.craft.closedblocks.api.material.MaterialProxy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class MaterialsLookup implements MaterialLookupService {

    private final Plugin plugin;

    // No, there's not a ConcurrentWeakHashMap, unfortunately...
    private final Map<NamespacedKey, WeakReference<ProxiedMaterial>> vanillaMaterialsCache = new ConcurrentHashMap<>();

    @NotNull
    private final Registry<Material> materialRegistry = Objects.requireNonNull(Bukkit.getRegistry(Material.class),
            "Could not find material registry, which is impossible. Are we running on a bukkit based server?");

    public MaterialsLookup(final @NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get a material. The ClosedBlocks proxy
     * instance is returned in the case of vanilla material. But if
     * no match is found, an empty optional will be
     * returned instead. This method can also be used to
     * determine if a material exists or is a valid
     * material (using {@link Optional#isEmpty()} or {@link Optional#isPresent()})
     *
     * @param key the material namespace
     * @return the material proxy
     */
    @Override
    public Optional<MaterialProxy> getMaterial(final @NotNull NamespacedKey key) {
        if (this.vanillaMaterialsCache.containsKey(key) || this.test(key)) {
            return Optional.ofNullable(
                    this.vanillaMaterialsCache.compute(key, (k, v) -> {
                        if (v == null || v.get() == null) {
                            return new WeakReference<>(new ProxiedMaterial(this.plugin, this.materialRegistry.get(key)));
                        }

                        return v;
                    }).get()
            );
        }

        return this.getServices().stream()
                .filter(service -> service.test(key))
                .findFirst()
                .flatMap(s -> s.getMaterial(key));
    }

    private Collection<MaterialLookupService> getServices() {
        return Bukkit.getServicesManager().getRegistrations(MaterialLookupService.class)
                .stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(service -> !service.equals(this))
                .collect(Collectors.toList());
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param namespacedKey the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final @NotNull NamespacedKey namespacedKey) {
        return this.materialRegistry.get(namespacedKey) != null;
    }
}