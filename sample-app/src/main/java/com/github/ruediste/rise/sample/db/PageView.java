package com.github.ruediste.rise.sample.db;

import javax.inject.Inject;

import com.github.ruediste.rise.mvc.IControllerMvc;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePageTemplate;
import com.github.ruediste.rise.sample.SamplePageTemplate.SamplePageTemplateParameters;
import com.github.ruediste.rise.sample.ViewMvc;
import com.github.ruediste1.i18n.lString.LString;

public abstract class PageView<TController extends IControllerMvc, TData>
        extends ViewMvc<TController, TData>
        implements SamplePageTemplateParameters {

    @Inject
    SampleBundle bundle;

    @Inject
    SamplePageTemplate template;

    @Override
    public LString getTitle() {
        return label(this);
    }

    @Override
    public final void render(SampleCanvas html) {
        template.renderOn(html, this);
    }

}
