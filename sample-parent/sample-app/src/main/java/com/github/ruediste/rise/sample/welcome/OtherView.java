package com.github.ruediste.rise.sample.welcome;

import javax.inject.Inject;

import com.github.ruediste.rise.mvc.MvcUtil;
import com.github.ruediste.rise.sample.PageView;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
@Label("Yet another View")
public class OtherView extends PageView<WelcomeController, String> {

    @Inject
    MvcUtil util;

    @Override
    public void renderContent(SampleCanvas html) {
        html.h1().content("TheOther").bButtonA().HREF(go().index()).content("index");
    }
}
