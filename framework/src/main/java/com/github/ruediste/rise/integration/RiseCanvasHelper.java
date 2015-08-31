package com.github.ruediste.rise.integration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CMixedRender;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
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
    CoreConfiguration coreConfiguration;

    @Inject
    Injector injector;

    private ByteArrayOutputStream baos;
    private HtmlCanvasTarget target;
    private boolean isComponent;
    private CMixedRender cRender;

    public void rCssLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.CSS)
                return;
            html.link().REL("stylesheet").TYPE("text/css")
                    .HREF(mapper.getPathInfo(asset));
        });
    }

    public void rJsLinks(RiseCanvas<?> html, AssetBundleOutput output) {
        output.forEach(asset -> {
            if (asset.getAssetType() != DefaultAssetTypes.JS)
                return;
            html.script().SRC(util.url(mapper.getPathInfo(asset)))._script();
        });
    }

    public CoreUtil getUtil() {
        return util;
    }

    /**
     * Add a component to the canvas. Only available in component mode.
     */
    public void add(Component c) {
        checkComponent();
        target.commitAttributes();
        commitBuffer();
        cRender.add(c);
    }

    /**
     * Commit the current content of the output stream to the
     * {@link #getcRender()}
     */
    public void commitBuffer() {
        checkComponent();
        target.flush();
        if (baos.size() > 0) {
            cRender.add(baos.toByteArray());
            baos.reset();
        }
    }

    private void checkComponent() {
        if (!isComponent)
            throw new RuntimeException(
                    "Canvas was not initialized for component mode. Operation not allowed");
    }

    public void initializeForComponent(ByteArrayOutputStream baos,
            HtmlCanvasTarget target) {
        this.baos = baos;
        this.target = target;
        cRender = new CMixedRender();
        isComponent = true;
    }

    /**
     * Initialize the heleper for component rendering
     */
    public void initializeForOutput(ByteArrayOutputStream baos,
            HtmlCanvasTarget target) {
        this.baos = baos;
        this.target = target;
    }

    public void writeRaw(byte[] buffer) {
        target.commitAttributes();
        target.flush();
        try {
            baos.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CMixedRender getcRender() {
        checkComponent();
        return cRender;
    }

    public boolean isComponent() {
        return isComponent;
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

    public void renderComponent(Component c, RiseCanvas<?> html) {
        componentUtil.render(c, html);
    }

    /**
     * @see RiseCanvas#TEST_NAME(String)
     */
    public void TEST_NAME(String name) {
        if (coreConfiguration.isRenderTestName()
                && !Strings.isNullOrEmpty(name)) {
            target.addAttribute("data-test-name", name);
        }
    }

    public <T> T getInstance(Class<T> instanceClass) {
        return injector.getInstance(instanceClass);
    }
}
