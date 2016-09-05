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
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetHelper;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;
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
    AssetRequestMapper mapper;

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
    AssetHelper assetPipelineHelper;

    @Inject
    Authz authz;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    ComponentViewRepository componentViewRepository;

    public void rCssLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.CSS)
                return;
            html.link().REL("stylesheet").TYPE("text/css").HREF(assetPipelineHelper.url(asset));
        });
    }

    public void rJsLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.JS)
                return;
            html.script().SRC(assetPipelineHelper.url(asset))._script();
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

    @SuppressWarnings("unchecked")
    public <T> T getControllerAuthzInstance(Class<T> instanceClass) {
        Class<?> implClass = coreConfiguration.getRequestMapper(instanceClass)
                .getControllerImplementationClass(instanceClass);
        return (T) injector.getInstance(implClass);
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
