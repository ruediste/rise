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
                        html.render(navRenderer.navbar(navigations.sideNavigation,"top-nav",x->{}))
                       
                        .bContainer_fluid()
                            .bRow()
                                .bCol(x->x.xs(12).sm(2))
                                    .render(navRenderer.side(navigations.sideNavigation,x->{}))
                                ._bCol()
                                .bCol(x->x.xs(12).sm(10));
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
