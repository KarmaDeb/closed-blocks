package dev.tagless.craft.closedblocks.api.util;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

/**
 * StackWalker helper class
 */
public final class StackUtils {

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    StackUtils() {}

    @NotNull
    public Optional<Class<?>> getExternalCaller() {
        return WALKER.walk(stream -> stream
                .filter(frame -> isCurrentJar(frame.getDeclaringClass()))
                .findFirst()
                .map(StackWalker.StackFrame::getDeclaringClass));
    }

    @NotNull
    public Path getClassDeclaringFile(final @NotNull Class<?> clazz) {
        return Path.of(clazz.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()).toAbsolutePath();
    }

    private boolean isCurrentJar(final @NotNull Class<?> clazz) {
        String jarPath = StackUtils.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        String callerPath = clazz
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        return jarPath.equals(callerPath);
    }
}
