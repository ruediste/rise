package laf.base;

import java.lang.annotation.*;

import javax.inject.Qualifier;

@Qualifier
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
