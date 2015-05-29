package com.github.ruediste.rise.sample.welcome;

import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.component.SampleComponentController;
import com.github.ruediste.rise.sample.db.PageView;
import com.github.ruediste.rise.sample.db.TodoController;

public class WelcomeView extends
        PageView<WelcomeController, WelcomeController.Data> {

    @Override
    public String getTitle() {
        return "Welcome";
    }

    @Override
    public void renderBody(SampleCanvas html) {
        html.bButtonA().HREF(go().other()).content("other");
        html.bButtonA().HREF(go(TodoController.class).index())
                .content("Todo Items");
        html.bButtonA().HREF(go().error()).content("Page with error");
        html.bButtonA().HREF(go(SampleComponentController.class).index())
                .span().CLASS("glyphicon glyphicon-search")._span()
                .content("Component Sample");

    }
}
