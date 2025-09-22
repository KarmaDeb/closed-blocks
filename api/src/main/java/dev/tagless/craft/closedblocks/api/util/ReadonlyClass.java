package dev.tagless.craft.closedblocks.api.util;

/**
 * A readonly class defines a class which is
 * intended to provide only "static" methods
 * in a non-extensible way.
 */
public abstract class ReadonlyClass {

    /**
     * Instantiate the class
     * @throws IllegalStateException always
     */
    protected ReadonlyClass() {
        throw new IllegalStateException(String.format("Cannot instantiate read-only class %s", this.getClass().getSimpleName()));
    }
}
