package com.github.ruediste.rise.integration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.component.components.CMixedRender;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.rise.core.web.assetPipeline.DefaultAssetTypes;

public class RiseCanvasHelper {

    @Inject
    private CoreUtil util;

    @Inject
    AssetRequestMapper mapper;

    @Inject
    CurrentLocale currentLocale;

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

}
