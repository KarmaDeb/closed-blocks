package dev.tagless.craft.closedblocks.api.util;

import dev.tagless.craft.closedblocks.api.util.reflection.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Utils helper class
 */
public final class Utils extends ReadonlyClass {

    private static final SoundUtils SOUND_UTILS = new SoundUtils();
    private static final StringUtils STRING_UTILS = new StringUtils();
    private static final StackUtils STACK_UTILS = new StackUtils();
    private static final ReflectionUtils REFLECTION_UTILS = new ReflectionUtils();

    @NotNull
    public static SoundUtils sound() {
        return SOUND_UTILS;
    }

    @NotNull
    public static StringUtils string() {
        return STRING_UTILS;
    }

    @NotNull
    public static StackUtils stack() {
        return STACK_UTILS;
    }

    @NotNull
    public static ReflectionUtils reflection() {
        return REFLECTION_UTILS;
    }
}
