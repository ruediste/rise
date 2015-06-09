package com.github.ruediste.rise.core;

import java.util.Locale;

import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Provides access to the current default locale. This is typically implemented
 * by a session-scoped object.
 * 
 */
@ImplementedBy(DefaultCurrentLocale.class)
public interface CurrentLocale {

    Locale getCurrentLocale();

    void setCurrentLocale(Locale locale);
}
