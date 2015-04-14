package com.github.ruediste.laf.core.scopes;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.inject.Scope;

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Scope
@Documented
public @interface SessionScoped {

}
