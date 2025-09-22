package dev.tagless.craft.closedblocks.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a method provides the final default implementation.
 * It suggests that the annotated method cannot or should not be overridden in subclasses,
 * as it represents a final behavior within the context it is used.
 * <p/>
 * The annotation is applied at the method level and is retained at runtime,
 * allowing reflective access to determine if a method has been marked with this behavior.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FinalDefaultImplementation {
}
