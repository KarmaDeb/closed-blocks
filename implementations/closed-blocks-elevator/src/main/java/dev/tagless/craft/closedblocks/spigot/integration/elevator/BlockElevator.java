package dev.tagless.craft.closedblocks.spigot.integration.elevator;

import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.block.EvolvedBlock;
import dev.tagless.craft.closedblocks.api.block.Traversable;
import dev.tagless.craft.closedblocks.api.block.evolution.Elevator;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.BlockBehavior;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.ProxiedBlockBehavior;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.BlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.block.evolution.behaviour.registry.EvolvedBlockBehaviorRegistry;
import dev.tagless.craft.closedblocks.api.material.MaterialLookupService;
import dev.tagless.craft.closedblocks.api.material.MaterialProxy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BlockElevator implements Elevator {

    private final OfflinePlayer player;
    private final Traversable<BlockElevator> traversable;

    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    private String name;
    private NamespacedKey material;

    public BlockElevator(final @NotNull Player player, final @NotNull Block block) {
        this.player = Bukkit.getOfflinePlayer(player.getUniqueId());

        this.world = block.getWorld().getUID();
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();

        this.traversable = ElevatorTraversableManager.get(block.getWorld(), block.getX(), block.getZ());
        this.traversable.add(this);
    }

    /**
     * Get the current floor name. Can be empty,
     * in which case, and if enabled, nothing
     * will be shown in the title screen when
     * using the elevator except for the
     * floor number
     *
     * @return the floor number
     */
    @Override
    public @NotNull String getFloorName() {
        return this.name;
    }

    /**
     * Set the current floor name. Can be empty
     *
     * @param value the floor name
     */
    @Override
    public void setFloorName(final @NotNull String value) {
        this.name = value;
    }

    /**
     * Get the current floor level. Usually matches
     * {@link dev.tagless.craft.closedblocks.api.block.evolution.Navigable#getCurrent()}, but this is not
     * a requirement for Elevators.
     *
     * @return the floor level of the
     * elevator
     */
    @Override
    public int getDisplayFloor() {
        return this.traversable.displayIndex(this);
    }

    /**
     * Set the current floor level. By default, this
     * method does nothing because of the nature of
     * {@link #getDisplayFloor()}. Changing a floor
     * number will not change the block index
     *
     * @param index the display floor number
     */
    @Override
    public void setDisplayFloor(final int index) {
        this.traversable.setDisplayIndex(this, index);
    }

    /**
     * Get the minimum index of the navigation sequence. Usually
     * zero, but there's no restriction on it.
     *
     * @return the minimum index
     */
    @Override
    public int getMin() {
        return 0;
    }

    /**
     * Get the current index of the navigation sequence.
     *
     * @return the current index
     */
    @Override
    public int getCurrent() {
        return this.traversable.index(this);
    }

    /**
     * Get the maximum index of the navigation sequence. Usually
     * the number of blocks in the sequence, but there's no
     * restriction on it.
     *
     * @return the maximum index
     */
    @Override
    public int getMax() {
        return this.traversable.getSize() - 1;
    }

    /**
     * Get the previous block in the navigation sequence
     *
     * @return the previous block
     */
    @Override
    public @NotNull Optional<? extends Elevator> previous() {
        return this.traversable.previous(this);
    }

    /**
     * Get the next block in the navigation sequence
     *
     * @return the next block
     */
    @Override
    public @NotNull Optional<? extends Elevator> next() {
        return this.traversable.next(this);
    }

    /**
     * Get the next block in the navigation sequence. If the
     * amount is negative, the sequence will be "played"
     * backwards
     *
     * @param number the number of steps to take
     * @return the next or previous block in the
     * specified steps
     */
    @Override
    public @NotNull Optional<BlockElevator> step(final int number) {
        return this.traversable.step(this, number);
    }

    /**
     * Get the block world. This method can
     * return an empty optional if the world is yet
     * not loaded
     *
     * @return the world of the block
     */
    @Override
    public @NotNull Optional<World> getWorld() {
        return Optional.ofNullable(
                Bukkit.getWorld(this.world)
        );
    }

    /**
     * Get the player who placed the block
     *
     * @return the player who placed the block
     */
    @Override
    public @NotNull OfflinePlayer getOwner() {
        return this.player;
    }

    /**
     * Get the block material. The value returned by
     * this method is a namespaced key to
     * ensure support with any other plugin that
     * extends the vanilla materials introducing
     * new ones.
     *
     * @return the block material
     */
    @Override
    public @NotNull NamespacedKey getMaterial() {
        return this.material;
    }

    /**
     * Set the block material. If the material is anything
     * but a block, the material will be ignored
     *
     * @param material the material to replace with
     */
    @Override
    public void setMaterial(final @NotNull NamespacedKey material) {
        MaterialLookupService service = MaterialLookupService.getInstance();
        service.getMaterial(material).ifPresent(proxy -> {
            if (proxy.isBlock() && proxy.isSolid()) {
                this.material = material;
            }
        });
    }

    /**
     * Get the block X position
     * in the world
     *
     * @return the block X position
     */
    @Override
    public int getX() {
        return this.x;
    }

    /**
     * Get the block Y position
     * in the world
     *
     * @return the block Y position
     */
    @Override
    public int getY() {
        return this.y;
    }

    /**
     * Get the block Z position
     * in the world
     *
     * @return the block Z position
     */
    @Override
    public int getZ() {
        return this.z;
    }

    /**
     * Get a behavior of the block
     *
     * @param behaviorType the behavior type
     * @return the block behavior
     */
    @Override
    public @NotNull @Unmodifiable <T extends BlockBehavior> Optional<T> getBehavior(final @NotNull Class<T> behaviorType) {
        EvolvedBlockBehaviorRegistry registry = this.getBehaviorRegistry();
        BlockBehaviorRegistry behaviors = registry.get(Elevator.class).orElseThrow();

        return behaviors.getAll().stream()
                .filter(behaviorType::isAssignableFrom)
                .findFirst()
                .map(type -> {
                    try {
                        Constructor<? extends BlockBehavior> constructor = type.getConstructor(EvolvedBlock.class);
                        return behaviorType.cast(constructor.newInstance(this));
                    } catch (ReflectiveOperationException ex) {
                        throw new IllegalStateException(ex);
                    }
                });
    }

    /**
     * Get all the behaviors this block
     * provides
     *
     * @return the block behaviors
     */
    @Override
    public @NotNull ProxiedBlockBehavior<? extends BlockBehavior> getBehavior() {
        EvolvedBlockBehaviorRegistry registry = this.getBehaviorRegistry();
        BlockBehaviorRegistry behaviors = registry.get(Elevator.class).orElseThrow();

        return new ProxiedBlockBehavior<>(this, behaviors.getAll());
    }

    /**
     * Removes the block. Unlike the {@link #destroy(Player, ItemStack) destroy} method,
     * this method DOES NOT trigger any event, and does not perform any validation.
     */
    @Override
    public void remove() {

    }

    /**
     * Get if the player can destroy the block
     *
     * @param player the player trying to destroy the block
     * @param tool   the tool (item) the player is holding. For a generic
     *               "break-permission" check, this should be set to null
     * @return if the player is able to destroy
     * the block
     */
    @Override
    public boolean canDestroy(final @NotNull Player player, final @Nullable ItemStack tool) {
        return true;
    }

    /**
     * Get if the player can destroy the block with
     * a tool of the provided material
     *
     * @param player   the player trying to destroy the block
     * @param material the tool (item) material the player could
     *                 have
     * @return if the player is able to destroy
     * the block
     */
    @Override
    public boolean canDestroy(final @NotNull Player player, final @NotNull NamespacedKey material) {
        return true;
    }

    /**
     * Destroys the block. Unlike the {@link #remove() remove} method,
     * this method DOES trigger an event, and performs pre-validations before
     * breaking the block
     *
     * @param player the player trying to destroy the block
     * @param item   the item the player is currently holding. If the player
     *               is holding nothing, this can be set to null
     * @return if the block could be destroyed
     */
    @Override
    public boolean destroy(final @Nullable Player player, final @Nullable ItemStack item) {
        return false;
    }

    /**
     * Place the block in the world. This method should be
     * called just once unless {@link #destroy(Player, ItemStack)} has been
     * previously called, and in case the block is already placed,
     * further calls to this method should be completely ignored
     *
     * @param player the player who placed the block
     * @param block  the block in the world in where the evolved
     *               block was placed
     */
    @Override
    public boolean place(final @NotNull Player player, final @NotNull Block block) {
        if (!block.getWorld().getUID().equals(this.world) ||
                block.getX() != this.x ||
                block.getY() != this.y ||
                block.getZ() != this.z ||
                !player.getUniqueId().equals(this.player.getUniqueId())) {
            return false;
        }

        MaterialLookupService service = MaterialLookupService.getInstance();

        Optional<MaterialProxy> materialProxy = service.getMaterial(this.material);
        if (materialProxy.isEmpty()) {
            return false;
        }

        MaterialProxy material = materialProxy.get();
        return material.generateBlock(block.getWorld(), block.getX(), block.getY(), block.getZ()).map(placed -> {
            PersistentDataContainer container = placed.getChunk().getPersistentDataContainer();
            container.set(this.generateKey("owner"), PersistentDataType.STRING, this.player.getUniqueId().toString());
            container.set(this.generateKey("disguise"), PersistentDataType.STRING, this.material.toString());
            // This data is needed later for re-construction of elevator instance

            return true;
        }).orElse(false);
    }

    private EvolvedBlockBehaviorRegistry getBehaviorRegistry() {
        return ClosedBlocksAPI.getInstance()
                .getRegistry(EvolvedBlockBehaviorRegistry.class)
                .orElseThrow(() -> new IllegalStateException("EvolvedBlockBehaviorRegistry not available"));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an <dfn>{@index "equivalence relation"}</dfn>
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     *
     * <p>
     * An equivalence relation partitions the elements it operates on
     * into <i>equivalence classes</i>; all the members of an
     * equivalence class are equal to each other. Members of an
     * equivalence class are substitutable for each other, at least
     * for some purposes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @implSpec The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * In other words, under the reference equality equivalence
     * relation, each equivalence class only has a single element.
     * @apiNote It is generally necessary to override the {@link #hashCode() hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     * <p>The two-argument {@link Objects#equals(Object,
     * Object) Objects.equals} method implements an equivalence relation
     * on two possibly-null object references.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockElevator elevator)) {
            return false;
        }

        return this.x == elevator.getX() &&
                this.y == elevator.getY() &&
                this.z == elevator.getZ() &&
                this.world.equals(elevator.getWorld().map(World::getUID).orElse(null));
    }

    /**
     * {@return a hash code value for this object} This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@link
     *     #equals(Object) equals} method, then calling the {@code
     *     hashCode} method on each of the two objects must produce the
     *     same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link #equals(Object) equals} method, then
     *     calling the {@code hashCode} method on each of the two objects
     *     must produce distinct integer results.  However, the programmer
     *     should be aware that producing distinct integer results for
     *     unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @apiNote The {@link Objects#hash(Object...) hash} and {@link
     * Objects#hashCode(Object) hashCode} methods of {@link
     * Objects} can be used to help construct simple hash codes.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z);
    }

    private NamespacedKey generateKey(final String key) {
        return NamespacedKey.fromString(String.format("cbe:elevator_%d_%d_%d_%s",
                this.x,
                this.y,
                this.z,
                key));
    }
}