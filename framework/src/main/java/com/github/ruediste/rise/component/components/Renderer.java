package com.github.ruediste.rise.component.components;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

public interface Renderer {

    void accept(HtmlCanvas html) throws IOException;
}
