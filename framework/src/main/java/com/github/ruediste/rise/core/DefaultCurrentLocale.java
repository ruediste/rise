package com.github.ruediste.rise.core;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.ruediste.rise.core.scopes.SessionScoped;

/**
 * Default implementation of {@link CurrentLocale}
 */
@SessionScoped
public class DefaultCurrentLocale implements CurrentLocale {

    @Inject
    CoreConfiguration config;

    private Locale currentLocale;

    @PostConstruct
    void postConstruct(CoreRequestInfo info) {
        currentLocale = config.getDefaultLocale();

    }

    @Override
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    @Override
    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

}
