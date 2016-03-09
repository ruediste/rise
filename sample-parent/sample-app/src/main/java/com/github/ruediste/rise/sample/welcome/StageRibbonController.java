package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.integration.StageRibbonControllerBase;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePageTemplate;
import com.github.ruediste.rise.sample.SamplePageTemplate.SamplePageTemplateParameters;
import com.github.ruediste.rise.sample.ViewMvc;
import com.github.ruediste1.i18n.lString.LString;

public class StageRibbonController
        extends StageRibbonControllerBase<StageRibbonController> {

    private static class View extends ViewMvc<StageRibbonController, Data> {

        @Inject
        SamplePageTemplate template;

        //@formatter:off
        @Override
        public void render(SampleCanvas html)  {
            template.renderOn(html, new SamplePageTemplateParameters() {
                
                @Override
                public void renderContent(SampleCanvas html) {
                    data().renderDefaultView(html);
                }
                
                @Override
                public LString getTitle() {
                    return data().getTitle();
                }
                
                @Override
                public boolean isRenderRaw() {
                    return true;
                }
            });
        }
        //@formatter:on
    }

    @Override
    protected ActionResult showView(Data data) {
        return view(View.class, data);
    }

}
