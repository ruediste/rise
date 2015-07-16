package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.ContentRenderResult;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CImgTemplate extends ComponentTemplateBase<CImg> {

    @Override
    public void doRender(CImg component, RiseCanvas<?> html) {
        html.img().SRC(getAjaxUrl(component)).ALT(component.getAlt());
    }

    @Inject
    CoreRequestInfo info;

    @Override
    public HttpRenderResult handleAjaxRequest(CImg component, String suffix)
            throws Throwable {

        return new ContentRenderResult(component.getSource().get(),
                "image/jpeg");
    };
}
