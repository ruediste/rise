package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.db.PageView;

public class OtherView extends PageView<WelcomeController, String> {

    @Inject
    MvcUtil util;

    @Override
    public String getTitle() {
        return "Other Controller";
    }

    @Override
    public void renderBody(SampleCanvas html) {
        html.h1().content("TheOther").bButtonA().HREF(go().index())
                .content("index");
    }
}
