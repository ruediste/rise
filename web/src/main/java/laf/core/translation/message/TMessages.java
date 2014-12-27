package laf.core.translation.message;

import java.lang.annotation.*;

import org.apache.deltaspike.partialbean.api.PartialBeanBinding;

/**
 * Marker interface for
 */
@PartialBeanBinding
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TMessages {

}
