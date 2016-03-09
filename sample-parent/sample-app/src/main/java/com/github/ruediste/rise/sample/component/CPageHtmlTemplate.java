package com.github.ruediste.rise.sample.component;

import javax.inject.Inject;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.sample.ComponentTemplate;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePageTemplate;
import com.github.ruediste.rise.sample.SamplePageTemplate.SamplePageTemplateParameters;
import com.github.ruediste1.i18n.lString.LString;

public class CPageHtmlTemplate extends ComponentTemplate<CPage> {

    @Inject
    SampleBundle bundle;

    @Inject
    SamplePageTemplate template;

    @Override
    public void doRender(CPage component, SampleCanvas html) {
        template.renderOn(html, new SamplePageTemplateParameters() {

            @Override
            public void renderContent(SampleCanvas html) {
                html.renderChildren(component);
            }

            @Override
            public LString getTitle() {
                LString title = component.getTitle();

                return title == null ? (locale -> "") : title;
            }
        });
    }
}
