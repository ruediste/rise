package com.github.ruediste.rise.sample.fileupload;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFileInput;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.sample.ViewComponent;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

public class FileUploadController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<FileUploadController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this)).add(toComponent(html -> html.h1()
                    .content("File Upload Demo").add(new CFileInput())
                    .add(new CButton("reload"))));
        }

    }

    @Label("File Upload Demo")
    public ActionResult index() {
        return null;
    }
}
