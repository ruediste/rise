package com.github.ruediste.rise.sample.component;

import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CReload;
import com.github.ruediste.rise.sample.ComponentTemplate;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePageTemplate;
import com.github.ruediste.rise.sample.SamplePageTemplate.SamplePageTemplateParameters;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public class CPageHtmlTemplate extends ComponentTemplate<CPage> {

    @Inject
    SampleBundle bundle;

    @Inject
    SamplePageTemplate template;

    @Inject
    LabelUtil labelUtil;

    @Inject
    ComponentPage page;

    @Override
    public void doRender(CPage component, SampleCanvas html) {
        template.renderOn(html, new SamplePageTemplateParameters() {

            @Override
            public void renderContent(SampleCanvas html) {
                html.add(new CReload(() -> html.render(component.body())));
            }

            @Override
            public LString getTitle() {
                Optional<LString> title = Optional.ofNullable(component.getTitle());
                if (!title.isPresent()) {
                    title = labelUtil.type(page.getView().getClass()).tryLabel().map(x -> (LString) x);
                }

                return title.orElseGet(() -> (locale -> ""));
            }
        });
    }
}
