package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.web.RedirectRenderResult;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste1.i18n.lString.LString;

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

    @Inject
    MvcUtil util;

    protected class Data {
        public UrlSpec returnUrl;

        public void renderDefaultView(BootstrapRiseCanvas<?> html) {
            Data data = this;
            html.bContainer().bRow().bCol(x -> x.xs(12)).h1().BtextCenter()
                    .STYLE("color:" + stage.color + ";background:"
                            + stage.backgroundColor)
                    .content(getTitleString())._bCol()._bRow().bRow()
                    .bCol(x -> x.xs(12).sm(6)).BtextCenter().a()
                    .CLASS("btn btn-primary").HREF(data.returnUrl).span()
                    .CLASS("glyphicon glyphicon-arrow-left")._span()
                    .content("Go Back")._bCol();
            if (stage == ApplicationStage.DEVELOPMENT)
                html.div().CLASS("col-xs-12 col-sm-6 text-center").a()
                        .CLASS("btn btn-danger")
                        .HREF(util.go(StageRibbonControllerBase.class)
                                .dropAndCreateDataBase(data.returnUrl))
                        .span().CLASS("glyphicon glyphicon-refresh")._span()
                        .content("Drop-and-Create Database")._div();
            html._bRow()._bContainer();
        }

        public LString getTitle() {
            return r -> getTitleString();
        }

        public String getTitleString() {
            return stage + " Stage Ribbon Page";
        }
    }

    protected abstract ActionResult showView(Data data);

    public ActionResult index(UrlSpec returnUrl) {
        Data data = new Data();
        data.returnUrl = returnUrl;
        return showView(data);
    }

    @Updating
    public ActionResult dropAndCreateDataBase(UrlSpec returnUrl) {
        config.recreateDatabases();
        return new RedirectRenderResult(returnUrl);
    }

}
