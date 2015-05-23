package com.github.ruediste.rise.sample.welcome;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.api.ViewMvc;
import com.github.ruediste.rise.mvc.MvcUtil;

public class OtherView extends ViewMvc<WelcomeController, String> {

    @Inject
    MvcUtil util;

    @Override
    public void render(HtmlCanvas html) throws IOException {
        html.html().head()._head().body().h1().content("TheOther")
                .a(href(url(go().index()))).content("index")._body()._html();
    }

}
