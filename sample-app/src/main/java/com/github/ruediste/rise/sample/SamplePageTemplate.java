package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rise.core.navigation.NavigationRenderer;
import com.github.ruediste.rise.integration.PageTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;

public class SamplePageTemplate extends PageTemplateBase {

    @Inject
    SampleBundle bundle;

    @Inject
    RisePageTemplate<SampleCanvas> risePageTemplate;

    @Inject
    Navigations navigations;

    @Inject
    NavigationRenderer navRenderer;

    public void renderOn(SampleCanvas canvas,
            SamplePageTemplateParameters parameters) {
        // your drawing code
        // @formatter:off
        risePageTemplate.renderOn(canvas,
                new RisePageTemplateParameters<SampleCanvas>() {

                    @Override
                    protected void renderJsLinks(SampleCanvas html) {
                        html.rJsLinks(bundle.out);
                    }

                    @Override
                    protected void renderCssLinks(SampleCanvas html) {
                        html.rCssLinks(bundle.out);
                    }

                    @Override
                    protected void renderHead(SampleCanvas html) {
                        html.title().content(parameters.getTitle());
                    }

                    @Override
                    protected void renderBody(SampleCanvas html) {
                        html.render(navRenderer.nav(navigations.sideNavigation,"top", x->x.pills()))
                       
                        .bContainer_fluid()
                            .bRow()
                                .bCol(x->x.xs(2).sm(1))
                                    .render(navRenderer.nav(navigations.sideNavigation,"top", x->x.stacked()))
                                ._bCol()
                                .bCol(x->x.xs(10).sm(11));
                                    parameters.renderBody(html);
                                html._bCol()
                            ._bRow()
                        ._bContainer_fluid();
                    }
                });
        // @formatter:on
    }

    public interface SamplePageTemplateParameters {
        String getTitle();

        void renderBody(SampleCanvas html);
    }
}
