package com.github.ruediste.rise.integration;

import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.web.CoreAssetBundle;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.front.RestartCountHolder;
import com.github.ruediste.rise.util.MethodInvocation;

public class RisePageTemplate<TCanvas extends RiseCanvas<TCanvas>>
        extends PageTemplateBase {

    @Inject
    RestartCountHolder holder;

    @Inject
    CoreConfiguration coreConfig;

    @Inject
    ComponentConfiguration componentConfig;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Inject
    ComponentPage pageInfo;

    @Inject
    ApplicationStage stage;

    public void renderOn(TCanvas html,
            RisePageTemplateParameters<TCanvas> parameters) {

        String testName = null;
        {
            ActionInvocation<String> invocation = coreRequestInfo
                    .getStringActionInvocation();
            if (invocation != null) {
                MethodInvocation<String> methodInvocation = invocation.methodInvocation;
                String ctrlName = coreConfig
                        .getRequestMapper(methodInvocation.getInstanceClass())
                        .getControllerImplementationClass(
                                methodInvocation.getInstanceClass())
                        .getName();
                testName = ctrlName + "."
                        + methodInvocation.getMethod().getName();
            }
        }
        //@formatter:off
		html.doctypeHtml5().html(); parameters.addHtmlAttributes(html);
			html.head();
				parameters.renderDefaultMetaTags(html);
				parameters.renderHead(html);
				parameters.renderCssLinks(html);
		html._head()
		.body().DATA(CoreAssetBundle.bodyAttributeRestartQueryUrl,url(coreConfig.restartQueryPathInfo))
		       .DATA(CoreAssetBundle.bodyAttributeRestartNr,Long.toString(holder.get()))
		       .TEST_NAME(testName);
	        if (componentRequestInfo.isComponentRequest()) {
	            html.DATA(CoreAssetBundle.bodyAttributePageNr,Long.toString(pageInfo.getPageId()))
                    .DATA(CoreAssetBundle.bodyAttributeReloadUrl,url(componentConfig.getReloadPath()))
                    .DATA(CoreAssetBundle.bodyAttributeHeartbeatUrl,url(componentConfig.getHeartbeatPath()))
                    .DATA(CoreAssetBundle.bodyAttributeHeartbeatInterval, Objects.toString(componentConfig.getHeartbeatInterval()))
                    .DATA(CoreAssetBundle.bodyAttributeAjaxUrl,url(componentConfig.getAjaxPath()))
                    .DATA(CoreAssetBundle.bodyAttributeReloadNr,"0");
	        }
		    parameters.addBodyAttributes(html);
			parameters.renderBody(html);
			parameters.renderJsLinks(html);
		html._body()._html();
		//@formatter:on
    }

    public abstract static class RisePageTemplateParameters<TCanvas extends Html5Canvas<TCanvas>> {
        protected void renderDefaultMetaTags(TCanvas html) {
            //@formatter:off
            html.meta().CHARSET("UTF-8").meta().HTTP_EQUIV("X-UA-Compatible")
                    .addAttribute("content","IE=edge").meta().NAME("viewport")
                    .addAttribute("content","width=device-width, initial-scale=1");
            //@formatter:on
        }

        protected void addHtmlAttributes(TCanvas html) {
        }

        /**
         * Hook to add additional attributes to the body tag
         */
        protected void addBodyAttributes(TCanvas canvas) {
        }

        /**
         * Render the CSS links of the resource bundle
         */
        protected abstract void renderCssLinks(TCanvas html);

        /**
         * Render the JS links of the resource bundle
         */
        protected abstract void renderJsLinks(TCanvas html);

        /**
         * Render content of the head tag
         */
        protected abstract void renderHead(TCanvas html);

        /**
         * Render content of the body tag
         */
        protected abstract void renderBody(TCanvas html);
    }

    /**
     * create a renderable for the stage ribbon
     * 
     * @param isFixed
     *            if true, the ribbon is fixed to the top of the viewport,
     *            otherwise to the top of the page
     * @param urlPoducer
     *            produces the link to
     *            {@link StageRibbonControllerBase#index(UrlSpec)}
     */
    public Renderable<TCanvas> stageRibbon(boolean isFixed,
            Function<UrlSpec, ActionResult> urlPoducer) {
        return html -> {
            html.div()
                    .CLASS("rise-ribbon"
                            + (isFixed ? " rise-ribbon-fixed" : ""))
                    .STYLE("background: " + stage.backgroundColor)

                    .a().STYLE("color: " + stage.color)
                    .HREF(urlPoducer.apply(
                            coreRequestInfo.getRequest().createUrlSpec()))
                    .content(stage.toString())._div();
        };
    }
}
