package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.ViewMvc;

public class OtherView extends ViewMvc<WelcomeController, String> {

    @Inject
    MvcUtil util;

    @Override
    public void render(SampleCanvas html) {
        html.html().head()._head().body().h1().content("TheOther").a()
                .HREF(url(go().index())).content("index")._body()._html();
    }
}
