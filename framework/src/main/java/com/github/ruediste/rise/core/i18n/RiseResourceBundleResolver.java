package com.github.ruediste.rise.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste1.i18n.lString.DefaultResourceBundleResolver;
import com.github.ruediste1.i18n.lString.ResourceBundleResolver;

/**
 * Resource Bundle resolver reading the bundle name from
 * {@link CoreConfiguration#getTranslationsResourceBundleName()} upon
 * initialization
 */
@Singleton
public class RiseResourceBundleResolver implements ResourceBundleResolver {

    @Inject
    CoreConfiguration config;

    DefaultResourceBundleResolver delegate;

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        return delegate.getResourceBundle(locale);
    }

    public void initialize() {
        delegate = new DefaultResourceBundleResolver();
        delegate.initialize(config.getTranslationsResourceBundleName());
    }
}
