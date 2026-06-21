package dev.tagless.craft.closedblocks.api.util.reflection.reflected;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A reflected parameter specifies a parameter which can
 * either be or not be part of the constructor
 */
public class ReflectedParameter {

    private final Class<?> type;
    private final boolean optional;

    private final ReflectedParameter[] parameters;

    /**
     * Create a new reflected parameter
     * @param type the parameter type
     * @param optional if the parameter is required
     */
    private ReflectedParameter(final @NotNull Class<?> type, final boolean optional) {
        this.optional = optional;
        this.type = type;
        this.parameters = null;
    }

    /**
     * Create a new reflected parameter
     * @param parameters the parameters in group
     */
    public ReflectedParameter(final @NotNull ReflectedParameter... parameters) {
        this.optional = false;
        this.type = null;
        this.parameters = parameters;
    }

    /**
     * Get if the reflected parameter is a simple
     * parameter
     * @return if the parameter is simple
     */
    public boolean isSimple() {
        return this.parameters == null;
    }

    /**
     * Get if the reflected parameter is a group
     * of parameters
     * @return if the parameters is a group
     */
    public boolean isGroup() {
        return this.parameters != null;
    }

    /**
     * Get if the reflected parameter is a group
     * and additionally, it behaves as an "either"
     * group. An "either" group is nothing but a group
     * with only "optional" fields, making it behave
     * as "a group of parameters, from which: not A, it
     * MUST be B, if not B, it must be C (and so...), but never none or all"
     * @return if the group behaves as an "either" group
     */
    public boolean isEither() {
        if (this.parameters == null) {
            return false;
        }

        for (ReflectedParameter p : this.parameters) {
            if (p.isGroup()) {
                assert p.parameters != null;
                for (ReflectedParameter s : p.parameters) {
                    if (s.isGroup() && !s.isEither()) {
                        return false;
                    }
                }
                if (!p.isEither()) {
                    return false;
                }
            } else if (!p.isOptional()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get if the group represents simply a "no-args"
     * @return if the group represents a no-args
     */
    public boolean isNoArgs() {
        return this.parameters != null && this.parameters.length == 0;
    }

    /**
     * Get if the parameter is optional
     * @return if the parameter is optional
     */
    public boolean isOptional() {
        return this.optional;
    }

    /**
     * Get the parameter type
     * @return the parameter type
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Get the parameter group
     * @return the group
     */
    @NotNull
    public ReflectedParameter[] getGroup() {
        if (this.parameters == null) {
            return new ReflectedParameter[0];
        }

        return Arrays.copyOf(this.parameters, this.parameters.length);
    }

    /**
     * Create a new required parameter
     * @param type the parameter type
     * @return the parameter
     */
    public static ReflectedParameter required(final Class<?> type) {
        return new ReflectedParameter(type, false);
    }

    /**
     * Create a new optional parameter
     * @param type the parameter type
     * @return the parameter
     */
    public static ReflectedParameter optional(final Class<?> type) {
        return new ReflectedParameter(type, true);
    }

    /**
     * Create a new group of reflected parameters
     * @param parameters the parameters
     * @return the grouped parameters
     */
    public static ReflectedParameter group(final ReflectedParameter... parameters) {
        return new ReflectedParameter(parameters);
    }
}
