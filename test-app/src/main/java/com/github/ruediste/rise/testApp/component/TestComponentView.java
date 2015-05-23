package com.github.ruediste.rise.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.id;

import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CRender;
import com.github.ruediste.rise.component.components.CTag;
import com.github.ruediste.rise.component.tree.Component;

public class TestComponentView extends TestComponentViewBase {

    @Override
    protected Component createComponents() {
        return new CPage()

                .add(new CTag(html -> html.div(id("a"))).add(new CController(
                        controller.subControllerA)))

                .add(new CTag(html -> html.div(id("b"))).add(new CController(
                        controller.subControllerB)))

                .add(new CTag(html -> html.div(id("main"))).add(
                        new CRender(html -> html.span(id("mainValue")).content(
                                controller.entity.getValue()))).add(

                        new CButton("refresh").class_("refresh").handler(
                                controller::refresh)));
    }

}
