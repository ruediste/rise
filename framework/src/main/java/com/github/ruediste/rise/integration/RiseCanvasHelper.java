package com.github.ruediste.rise.integration;

import java.util.Locale;

import javax.inject.Inject;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.ComponentViewRepository;
import com.github.ruediste.rise.component.IViewQualifier;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste.rise.core.security.authorization.Authz;
import com.github.ruediste.rise.core.web.assetDir.AssetDirRequestMapper;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.base.Strings;

public class RiseCanvasHelper {

    @Inject
    CoreUtil util;

    @Inject
    LabelUtil labelUtil;

    @Inject
    IconUtil iconUtil;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    ComponentUtil componentUtil;

    @Inject
    ComponentPage componentPage;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    Injector injector;

    @Inject
    Authz authz;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    ComponentViewRepository componentViewRepository;

    @Inject
    AssetDirRequestMapper assetDirRequestMapper;

    public void rCssLinks(RiseCanvas<?> html, AssetBundle bundle) {
        assetDirRequestMapper.getCssUrls(bundle).forEach(url -> {
            html.link().REL("stylesheet").TYPE("text/css").HREF(url);
        });
    }

    public void rJsLinks(RiseCanvas<?> html, AssetBundle bundle) {
        assetDirRequestMapper.getJsUrls(bundle).forEach(url -> {
            html.script().SRC(url)._script();
        });
    }

    public CoreUtil getUtil() {
        return util;
    }

    public Locale getCurrentLocale() {
        return currentLocale.getCurrentLocale();
    }

    public LabelUtil getLabelUtil() {
        return labelUtil;
    }

    public IconUtil getIconUtil() {
        return iconUtil;
    }

    public ComponentUtil getComponentUtil() {
        return componentUtil;
    }

    /**
     * @see RiseCanvas#TEST_NAME(String)
     */
    public void TEST_NAME(RiseCanvas<?> html, String name) {
        if (coreConfiguration.isRenderTestName() && !Strings.isNullOrEmpty(name)) {
            html.DATA("test-name", name);
        }
    }

    public Authz getAuthz() {
        return authz;
    }

    public void renderController(RiseCanvas<?> html, Object controller, Class<? extends IViewQualifier> viewQualifier) {
        html.renderView(componentViewRepository.createView((SubControllerComponent) controller, false, viewQualifier));
    }

    public void renderController(RiseCanvas<?> html, Object controller) {
        html.renderView(componentViewRepository.createView((SubControllerComponent) controller, false, null));
    }

}
