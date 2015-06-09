package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste1.i18n.label.Label;

@Label("Yet another View")
public class OtherView extends PageView<WelcomeController, String> {

    @Inject
    MvcUtil util;

    @Override
    public void renderBody(SampleCanvas html) {
        html.h1().content("TheOther").bButtonA().HREF(go().index())
                .content("index");
    }
}
