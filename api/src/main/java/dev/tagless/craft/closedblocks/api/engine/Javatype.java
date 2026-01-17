package dev.tagless.craft.closedblocks.api.engine;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Represents a java type, used by the
 * script engine to dynamically allow different
 * package access
 */
public final class Javatype implements Predicate<String> {

    private static final Map<Class<?>, Collection<String>> discovered = new ConcurrentHashMap<>();

    private static final byte MODE_PACKAGE = 1 << 1;
    private static final byte MODE_INSTANCEOF = 1 << 2;
    private static final byte MODE_CLASS = 1 << 3;

    private final byte mode;
    private final String[] nodes;

    private Javatype(final byte mode, final String... nodes) {
        this.mode = mode;
        this.nodes = nodes;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param s the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final String s) {
        if (s == null) return false;
        try {
            Class<?> clazz = Class.forName(s);
            return switch (this.mode) {
                case MODE_PACKAGE -> clazz.getPackageName().startsWith(this.nodes[0]);
                case MODE_INSTANCEOF -> Class.forName(this.nodes[0]).isAssignableFrom(clazz);
                case MODE_CLASS -> Arrays.asList(this.nodes).contains(s);
                default -> false;
            };
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}

        return false;
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
        if (!(obj instanceof Javatype type)) return false;

        return Arrays.equals(this.nodes, type.nodes) && this.mode == type.mode;
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
        return Objects.hash(Arrays.hashCode(this.nodes), this.mode);
    }

    /**
     * Create a javatype for a package
     * @param packageName the package name
     * @return the javatype
     */
    @NotNull @Contract("null -> fail")
    public static Javatype pack(final String packageName) {
        String name = Objects.requireNonNull(packageName, "package name cannot be null");
        if (packageName.endsWith(".")) {
            return new Javatype(MODE_PACKAGE, name);
        }

        return new Javatype(MODE_PACKAGE, String.format("%s.", name));
    }

    /**
     * Create a javatype for a package
     * @param pack the package
     * @return the javatype
     */
    @NotNull @Contract("null -> fail")
    public static Javatype pack(final Package pack) {
        return pack(Objects.requireNonNull(pack, "package cannot be null").getName());
    }

    /**
     * Create a javatype which is a "wildcard" for
     * objects which are instanceof the provided
     * type
     * @param type the type
     * @return the javatype
     */
    @NotNull @Contract("null -> fail")
    public static Javatype instance(final Class<?> type) {
        return new Javatype(MODE_INSTANCEOF, Objects.requireNonNull(type, "type cannot be null").getCanonicalName());
    }

    /**
     * Create a javatype which recursively scans the
     * type to allow complete usage of it
     * @param type the type
     * @return the javatype
     */
    @NotNull @Contract("null -> fail")
    public static Javatype type(final Class<?> type) {
        Collection<String> strings = new LinkedHashSet<>();
        strings.add(Objects.requireNonNull(type, "type cannot be null").getSimpleName());
        mapAllowedPackagesFromJavaClass(type, strings, new HashSet<>());

        return new Javatype(MODE_CLASS, strings.toArray(new String[0]));
    }

    private static void mapAllowedPackagesFromJavaClass(final Class<?> clazz,
                                                 final Collection<String> out,
                                                 final Collection<Class<?>> visited) {
        if (discovered.containsKey(clazz)) {
            out.addAll(discovered.get(clazz));
            visited.add(clazz);

            return;
        }

        if (visited.add(clazz)) {
            Collection<String> classes = new HashSet<>();

            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                int modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    continue;
                }

                Class<?> returnType = method.getReturnType();
                if (returnType.equals(void.class)) {
                    continue;
                }

                String returnTypeClassName = returnType.getCanonicalName();
                if (out.stream().noneMatch(p -> p.startsWith(returnTypeClassName))) {
                    classes.add(returnTypeClassName);
                }

                mapAllowedPackagesFromJavaClass(returnType, out, visited);
            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    continue;
                }

                Class<?> returnType = field.getType();
                String returnTypeClassName = returnType.getCanonicalName();
                if (out.stream().noneMatch(p -> p.startsWith(returnTypeClassName))) {
                    classes.add(returnTypeClassName);
                }

                mapAllowedPackagesFromJavaClass(returnType, out, visited);
            }

            out.addAll(classes);
            discovered.put(clazz, classes);
        }
    }
}