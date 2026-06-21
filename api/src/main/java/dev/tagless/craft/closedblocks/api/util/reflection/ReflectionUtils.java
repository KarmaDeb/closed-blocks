package dev.tagless.craft.closedblocks.api.util.reflection;

import dev.tagless.craft.closedblocks.api.util.reflection.reflected.constructor.ReflectedConstructor;
import dev.tagless.craft.closedblocks.api.util.reflection.reflected.ReflectedParameter;
import dev.tagless.craft.closedblocks.api.util.reflection.reflected.constructor.ReflectedConstructors;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflection helper class
 */
public final class ReflectionUtils {

    private final Map<Class<?>, ReflectiveConfiguration<?>> configurationMap = new ConcurrentHashMap<>();

    public ReflectionUtils() {}

    /**
     * Finds a constructor
     * @param label the constructor label
     * @param clazz the class in where to find the constructor
     * @param paramTypes the expected parameter types
     * @return the reflected constructor
     * @param <T> the instance type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> Optional<ReflectedConstructor<T>> findConstructor(final String label,
                                                                 final Class<T> clazz,
                                                                 final Class<?>... paramTypes) {
        ReflectiveConfiguration<T> config = (ReflectiveConfiguration<T>)
                this.configurationMap.computeIfAbsent(clazz, p ->
                        new ReflectiveConfiguration<>((Class<T>) p));

        return config.lookup(label, paramTypes);
    }

    @SuppressWarnings({"unchecked", "rawtype"})
    public <T> ReflectedConstructors<T> findConstructors(final String label,
                                                         final Class<T> clazz,
                                                         final ReflectedParameter... paramTypes) {
        List<List<Class<?>>> rawCombinations = generateCombinationsForGroup(paramTypes, true);

        List<Class<?>[]> combinations = new ArrayList<>();
        for (List<Class<?>> combo : rawCombinations) {
            combinations.add(combo.toArray(new Class<?>[0]));
        }

        combinations.sort(Comparator.comparingInt(a -> a.length));

        List<ReflectedConstructor<T>> result = new ArrayList<>();
        int idx = 0;
        for (Class<?>[] combo : combinations) {
            Optional<ReflectedConstructor<T>> reflected = this.findConstructor(
                    String.format("%s#%d", label, idx++), clazz, combo);
            reflected.ifPresent(result::add);
        }

        result.sort(Comparator.comparingInt(ReflectedConstructor::length));
        ReflectedConstructor<T>[] a = (ReflectedConstructor<T>[]) new ReflectedConstructor[result.size()];
        for (int i = 0; i < result.size(); i++) {
            a[i] = result.get(i);
        }

        return new ReflectedConstructors<>(label, a);
    }

    private List<List<Class<?>>> generateCombinationsForGroup(final ReflectedParameter[] params, final boolean isEitherGroup) {
        List<List<Class<?>>> groupCombinations = new ArrayList<>();

        if (isEitherGroup) {
            List<Class<?>> requiredTypes = new ArrayList<>();
            List<List<Class<?>>> optionalChoices = new ArrayList<>();

            for (ReflectedParameter p : params) {
                List<List<Class<?>>> paramCombos = generateParameterCombinations(p);

                if (!p.isGroup() && !p.isOptional()) {
                    for (List<Class<?>> combo : paramCombos) {
                        requiredTypes.addAll(combo);
                    }
                } else {
                    optionalChoices.addAll(paramCombos);
                }
            }

            if (!optionalChoices.isEmpty()) {
                for (List<Class<?>> option : optionalChoices) {
                    List<Class<?>> singleChoice = new ArrayList<>(requiredTypes);
                    singleChoice.addAll(option);
                    groupCombinations.add(singleChoice);
                }
            } else if (!requiredTypes.isEmpty()) {
                groupCombinations.add(requiredTypes);
            }
        } else {
            groupCombinations.add(new ArrayList<>());

            for (ReflectedParameter p : params) {
                if (p.isNoArgs()) {
                    groupCombinations.add(new ArrayList<>());
                    continue;
                }

                List<List<Class<?>>> paramCombos = generateParameterCombinations(p);
                List<List<Class<?>>> nextCombinations = new ArrayList<>();

                for (List<Class<?>> currentAccumulated : groupCombinations) {
                    for (List<Class<?>> newParamCombo : paramCombos) {
                        List<Class<?>> merged = new ArrayList<>(currentAccumulated);
                        merged.addAll(newParamCombo);
                        nextCombinations.add(merged);
                    }
                }
                groupCombinations = nextCombinations;
            }
        }

        return groupCombinations;
    }

    private List<List<Class<?>>> generateParameterCombinations(final ReflectedParameter param) {
        List<List<Class<?>>> result = new ArrayList<>();

        if (param.isSimple()) {
            List<Class<?>> singleType = new ArrayList<>();
            singleType.add(param.getType());
            result.add(singleType);

            if (param.isOptional()) {
                result.add(new ArrayList<>());
            }
        } else {
            result.addAll(generateCombinationsForGroup(param.getGroup(), param.isEither()));
        }

        return result;
    }
}
