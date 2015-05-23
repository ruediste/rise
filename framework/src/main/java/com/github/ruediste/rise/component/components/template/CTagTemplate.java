package com.github.ruediste.rise.component.components.template;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.components.CTag;

public class CTagTemplate extends ComponentTemplateBase<CTag> {

    @Override
    public void doRender(CTag component, HtmlCanvas html) throws IOException {
        html.render(component.getTagRenderer());
        super.doRender(component, html);
        html.close();
    }
}
