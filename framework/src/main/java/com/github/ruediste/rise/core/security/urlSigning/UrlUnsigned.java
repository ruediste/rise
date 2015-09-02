package com.github.ruediste.rise.core.security.urlSigning;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method or parameter should not be signed in URLs.
 * 
 * <p>
 * By default, RISE adds a signature to all generated URLs and checks the
 * signature while parsing. The signature contains the whole URL, parameters, a
 * salt, a secret and the session ID. This provides protection against forced
 * browsing and CSRF. However, it prohibits bookmarking URLs.
 * 
 * <p>
 * When disabling URL signing on a method or parameter, be extra careful to
 * perform authorization checks and input validation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Documented
public @interface UrlUnsigned {

}
