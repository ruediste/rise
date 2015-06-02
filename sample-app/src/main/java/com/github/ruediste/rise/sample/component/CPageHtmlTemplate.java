package com.github.ruediste.rise.sample.component;

import javax.inject.Inject;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.sample.ComponentTemplate;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePageTemplate;
import com.github.ruediste.rise.sample.SamplePageTemplate.SamplePageTemplateParameters;

public class CPageHtmlTemplate extends ComponentTemplate<CPage> {

    @Inject
    SampleBundle bundle;

    @Inject
    SamplePageTemplate template;

    @Override
    public void doRender(CPage component, SampleCanvas html) {
        template.renderOn(html, new SamplePageTemplateParameters() {

            @Override
            public void renderBody(SampleCanvas html) {
                html.render(children(component));

            }

            @Override
            public String getTitle() {
                return component.getTitle();
            }
        });
    }
}
