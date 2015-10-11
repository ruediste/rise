package com.github.ruediste.rise.testApp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.core.security.authorization.MetaRequiresRight;

@MetaRequiresRight
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresRight {
    Rights[]value();
}
