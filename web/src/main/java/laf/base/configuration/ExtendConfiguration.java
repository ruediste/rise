package laf.base.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method of a {@link ConfigurationDefiner} extends the
 * configuration of other definer. Thus the {@link ConfigurationParameter}
 * passed to the method is already configured by the other
 * {@link ConfigurationValueProvider}s
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtendConfiguration {

}
