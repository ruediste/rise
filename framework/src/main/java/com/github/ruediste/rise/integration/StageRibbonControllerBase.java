package com.github.ruediste.rise.integration;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.internal.CharactersWriteable;
import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.api.ViewMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.integration.RisePageTemplate.RisePageTemplateParameters;
import com.github.ruediste.rise.mvc.Updating;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;

public abstract class StageRibbonControllerBase<TSelf extends StageRibbonControllerBase<TSelf>>
        extends ControllerMvc<TSelf> {

    @Inject
    Logger log;

    @Inject
    DataBaseLinkRegistry registry;

    @Inject
    CoreConfiguration config;

    @Inject
    ApplicationStage stage;

    private static class Data {
        Class<? extends StageRibbonControllerBase<?>> controllerClass;
        public String originPathInfo;
        public AssetBundleOutput assets;
    }

    private static class View extends
            ViewMvc<StageRibbonControllerBase<?>, Data> {

        @Inject
        ApplicationStage stage;

        @Inject
        RisePageTemplate renderer;

        //@formatter:off
        @Override
        public void render(HtmlCanvas html) throws IOException {
            setControllerClass(data().controllerClass);
            renderer.renderOn(html, new RisePageTemplateParameters() {

                @Override
				protected void renderJsLinks(HtmlCanvas html)
						throws IOException { 
					html.render(jsLinks(data().assets));
				}

				@Override
				protected void renderHead(HtmlCanvas html) throws IOException {
					html.title().content(stage + " Stage Ribbon Page");
				}

				@Override
				protected void renderCssLinks(HtmlCanvas html)
						throws IOException {
					html.render(cssLinks(data().assets));
				}

				@Override
				protected void renderBody(HtmlCanvas html) throws IOException {
					html.div(class_("container"))
						.div(class_("row"))
							.div(class_("col-xs-12"))
								.h1(class_("text-center").style("color:"+stage.color+";background:"+stage.backgroundColor)).content(stage + " Stage Ribbon Page")
							._div()
						._div()
						.div(class_("row"))
							.div(class_("col-xs-12 col-sm-6 text-center"))
								.a(class_("btn btn-primary")
										.href(url(new PathInfo(data().originPathInfo)))).span(class_("glyphicon glyphicon-arrow-left"))._span().content("Go Back")
							._div();
					        if (stage==ApplicationStage.DEVELOPMENT)
    							html.div(class_("col-xs-12 col-sm-6 text-center"))
    								.a(class_("btn btn-danger")
    										.href(url(go().dropAndCreateDataBase(data().originPathInfo)))).span(class_("glyphicon glyphicon-refresh"))._span().content("Drop-and-Create Database")
    							._div();
						html._div()	
					._div();
                }

                @Override
                protected CharactersWriteable htmlAttributes() {
                    return id("page-rise-ribbon");
                }
            });
        }
        //@formatter:on
    }

    @SuppressWarnings("unchecked")
    public ActionResult index(String originPathInfo) {
        Data data = new Data();
        data.controllerClass = (Class<? extends StageRibbonControllerBase<?>>) getClass();
        data.originPathInfo = originPathInfo;
        data.assets = getAssets();
        return view(View.class, data);
    }

    @Updating
    public ActionResult dropAndCreateDataBase(String originPathInfo) {
        if (stage == ApplicationStage.DEVELOPMENT) {
            log.info("Dropping and Creating DB schemas ...");
            registry.dropAndCreateSchemas();
            config.loadDevelopmentFixture();
            commit();
        } else {
            log.error("Stage is not development");
        }
        return new RedirectRenderResult(new PathInfo(originPathInfo));
    }

    /**
     * Return an asset output containing {@link CoreAssetBundle#out}
     */
    protected abstract AssetBundleOutput getAssets();
}
