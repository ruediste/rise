package com.github.ruediste.rise.sample.fileupload;

import javax.inject.Inject;

import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.assetDir.AssetDirRequestMapper;
import com.github.ruediste.rise.sample.ComponentTemplate;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.fileupload.viewerJs.ViewerJsDir;

public class CViewerJSTemplate extends ComponentTemplate<CViewerJS> {
    @Inject
    ViewerJsDir dir;

    @Inject
    AssetDirRequestMapper mapper;

    @Override
    public void doRender(CViewerJS component, SampleCanvas html) {
        html.iframe()
                .SRC(mapper.getPathInfoString(dir, "") + "index.html#"
                        + getAjaxUrl(component) + "/content.pdf")
                .WIDTH("566").HEIGHT("800").ALLOWFULLSCREEN("allowfullscreen")
                ._iframe();
    }

    // <iframe src = "/ViewerJS/#../demo/ohm2013.odp" width='400' height='300'
    // allowfullscreen webkitallowfullscreen></iframe>

    // width='724' height='1024'
    // width='566' height='800'
    // width='389' height='550'
    // width='297' height='210'

    @Override
    public HttpRenderResult handleAjaxRequest(CViewerJS component,
            String suffix) throws Throwable {
        return new ContentRenderResult(component.getSource(), r -> {
        });
    }
}
