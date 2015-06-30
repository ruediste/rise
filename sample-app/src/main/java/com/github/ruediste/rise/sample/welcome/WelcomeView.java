package com.github.ruediste.rise.sample.welcome;

import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.component.SampleComponentController;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste.rise.sample.db.TodoController;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
@Label("Welcome")
public class WelcomeView extends
        PageView<WelcomeController, WelcomeController.Data> {

    @Override
    public void renderBody(SampleCanvas html) {
        html.h1().content(label(this));
        html.bButtonA().HREF(go().other()).content("other");
        html.bButtonA().HREF(go(TodoController.class).index())
                .content("Todo Items");
        html.bButtonA().HREF(go().error()).content("Page with error");
        html.rButtonA(go(SampleComponentController.class).index(), a -> {
        });

    }
}
