package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.integration.RisePageTemplate;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.integration.StageRibbonControllerBase;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.sample.SampleBundle;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.ViewMvc;

public class StageRibbonController
        extends StageRibbonControllerBase<StageRibbonController> {

    @Inject
    Logger log;

    @Inject
    DataBaseLinkRegistry registry;

    @Inject
    CoreConfiguration config;

    @Inject
    ApplicationStage stage;

    private static class Data {
        public UrlSpec returnUrl;
    }

    private static class View extends ViewMvc<StageRibbonController, Data> {

        @Inject
        ApplicationStage stage;

        @Inject
        RisePageTemplate<SampleCanvas> template;

        @Inject
        SampleBundle bundle;

        //@formatter:off
        @Override
        public void render(SampleCanvas html)  {
            template.renderOn(html, new RisePageTemplateParameters<SampleCanvas>() {

                @Override
                protected void renderJsLinks(SampleCanvas html)
                        { 
                    html.rJsLinks(bundle.out);
                }

                @Override
                protected void renderHead(SampleCanvas html) {
                    html.title().content(stage + " Stage Ribbon Page");
                }

                @Override
                protected void renderCssLinks(SampleCanvas html)
                        {
                    html.rCssLinks(bundle.out);
                }

                @Override
                protected void renderBody(SampleCanvas html) {
                    html.bContainer()
                    .bRow()
                        .bCol(x->x.xs(12))
                          .h1().BtextCenter().STYLE("color:"+stage.color+";background:"+stage.backgroundColor)
                            .content(stage + " Stage Ribbon Page")
                        ._bCol()
                    ._bRow()
                    .bRow()
                        .bCol(x->x.xs(12).sm(6)).BtextCenter()
                            .a().CLASS("btn btn-primary")
                                    .HREF(data().returnUrl)
                                      .span().CLASS("glyphicon glyphicon-arrow-left")._span().content("Go Back")
                        ._bCol();
                        if (stage==ApplicationStage.DEVELOPMENT)
                            html.div().CLASS("col-xs-12 col-sm-6 text-center")
                                .a().CLASS("btn btn-danger")
                                        .HREF(go().dropAndCreateDataBase(data().returnUrl))
                                        .span().CLASS("glyphicon glyphicon-refresh")._span().content("Drop-and-Create Database")
                            ._div();
                    html._bRow() 
                ._bContainer();
                }

               
            });
        }
        //@formatter:on
    }

    public ActionResult index(UrlSpec returnUrl) {
        Data data = new Data();
        data.returnUrl = returnUrl;
        return view(View.class, data);
    }

    @Updating
    public ActionResult dropAndCreateDataBase(UrlSpec returnUrl) {
        if (stage == ApplicationStage.DEVELOPMENT) {
            log.info("Dropping and Creating DB schemas ...");
            registry.dropAndCreateSchemas();
            config.loadDevelopmentFixture();
        } else {
            log.error("Stage is not development");
        }
        return new RedirectRenderResult(returnUrl);
    }

}
