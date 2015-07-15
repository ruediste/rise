package com.github.ruediste.rise.component.components;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.integration.RiseCanvas;

public class CImgTemplate extends ComponentTemplateBase<CImg> {

    @Override
    public void doRender(CImg component, RiseCanvas<?> html) {
        html.img().SRC(getAjaxUrl(component)).ALT(component.getAlt());
    }

    @Inject
    CoreRequestInfo info;

    @Override
    public void handleAjaxRequest(CImg component, String suffix)
            throws Throwable {

        HttpServletResponse resp = info.getServletResponse();
        resp.setContentType("image/jpeg");
        ServletOutputStream out = resp.getOutputStream();
        out.write(component.getSource().get());
        out.close();
    };
}
