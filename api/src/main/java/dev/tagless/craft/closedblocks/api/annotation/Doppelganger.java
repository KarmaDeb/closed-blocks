package dev.tagless.craft.closedblocks.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class is, in essence, a "full proxy" of another
 * object, but which behaves different and contains specialized logic which also makes it "different"
 * from the proxied object.
 * <p/>
 * The annotation is applied at the class level and is retained at runtime,
 * allowing reflective access to determine if a class has been marked with this behavior.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Doppelganger {
}
