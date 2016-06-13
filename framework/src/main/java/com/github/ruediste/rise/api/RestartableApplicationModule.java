package com.github.ruediste.rise.api;

import javax.inject.Singleton;

import com.github.ruediste.rise.component.ComponentRestartableModule;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.integration.Stereotype;
import com.github.ruediste.rise.mvc.MvcRestartableModule;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.StereotypeAdditionalLabelExtractor;

public class RestartableApplicationModule extends AbstractModule {

    private Injector permanentInjector;

    public RestartableApplicationModule(Injector permanentInjector) {
        this.permanentInjector = permanentInjector;

    }

    @Override
    protected void configure() throws Exception {
        installMvcWebModule();
        installComponentModule();
        installCoreModule();
        installLoggerModule();

    }

    protected void installComponentModule() {
        install(new ComponentRestartableModule(permanentInjector));
    }

    protected void installMvcWebModule() {
        install(new MvcRestartableModule(permanentInjector));
    }

    protected void installCoreModule() {
        install(new CoreRestartableModule(permanentInjector));
    }

    protected void installLoggerModule() {
        install(new LoggerModule());
    }

    @Provides
    @Singleton
    public LabelUtil labelUtil(TranslatedStringResolver resolver) {
        LabelUtil result = new LabelUtil(resolver);
        result.setAdditionalLabelsExtractor(new StereotypeAdditionalLabelExtractor(Stereotype.class, result));
        return result;
    }

}
