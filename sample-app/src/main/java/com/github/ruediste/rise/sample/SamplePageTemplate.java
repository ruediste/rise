package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rise.core.navigation.NavigationRenderer;
import com.github.ruediste.rise.integration.PageTemplateBase;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.sample.welcome.LanguageController;
import com.github.ruediste.rise.sample.welcome.StageRibbonController;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.github.ruediste1.i18n.lString.LString;

/**
 * Template to render pages of the sample application.
 * 
 * Renders the assets and the header/menu structure. Used by both MVC (
 * {@link ViewMvc}) and Component ({@link ViewComponent}) pages.
 */
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
                        html.render(risePageTemplate.stageRibbon(false,x-> go(StageRibbonController.class).index(x)));
                        html.bNavbar("mainNav", opts->{}, ()->html.a().BNavbarBrand().HREF(go(WelcomeController.class).index()).content("RISE"), ()->{
                            navRenderer.renderNavItems(html, navigations.topNavigation)
                            .p().BnavbarText().BnavbarRight()
                              .a().BnavbarLink().HREF(go(LanguageController.class).switchLanguage("de")).content("de")
                              .a().BnavbarLink().HREF(go(LanguageController.class).switchLanguage("en")).content("en")
                            ._p();
                        })
                        .bContainer_fluid()
                            .bRow()
                                .bCol(x->x.xs(12));
                                    parameters.renderContent(html);
                                html._bCol()
                            ._bRow()
                        ._bContainer_fluid();
                    }
                });
        // @formatter:on
    }

    public interface SamplePageTemplateParameters {
        LString getTitle();

        /**
         * Overwrite and return true to skip rendering the navigation
         */
        default boolean isRenderRaw() {
            return false;
        }

        void renderContent(SampleCanvas html);
    }
}
