package com.github.ruediste.rise.sample.fileupload;

import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFileInput;
import com.github.ruediste.rise.component.components.CFileInput.UploadedFile;
import com.github.ruediste.rise.component.components.CImg;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSwitch;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.sample.ViewComponent;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

public class FileUploadController extends ControllerComponent {

    private enum Mode {
        LIST, UPLOAD
    }

    @Label("File Upload Demo")
    private static class MainView extends ViewComponent<FileUploadController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this))
                    .add(new CSwitch<Mode>().put(Mode.LIST, () -> {
                        return toComponent(
                                html -> html.bRow()
                                        .fForEach(controller.data().files,
                                                f -> html
                                                        .bCol(x -> x.sm(6)
                                                                .md(4))
                                                        .div().CLASS(
                                                                "thumbnail")
                                        .add(new CImg().setSource(f::getBytes))
                                        .add(new CViewerJS()
                                                .setSource(f.getBytes())).h2()
                                        .content(f.getSubmittedFileName())
                                        ._div()._bCol())._bRow()

                .add(new CButton(controller, c -> c.upload())));
                    }).put(Mode.UPLOAD, () -> {
                        CFileInput fileInput = new CFileInput();
                        return toComponent(html -> html.h1()
                                .content(label(MainView.class)).add(fileInput)
                                .add(new CButton(controller, c -> {
                            c.done(fileInput.getUploadedFiles());
                        })));
                    }).bind(() -> controller.data().getMode()));
        }
    }

    public static class Data {
        List<UploadedFile> files = new ArrayList<>();
        private Mode mode = Mode.UPLOAD;

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }
    }

    BindingGroup<Data> data = new BindingGroup<>(new Data());

    Data data() {
        return data.proxy();
    }

    @Labeled
    public void upload() {
        data.get().setMode(Mode.UPLOAD);
        data.pullUp();
    }

    @Labeled
    public void done(List<UploadedFile> uploadedFiles) {
        data.get().files.addAll(uploadedFiles);
        data.get().setMode(Mode.LIST);
        data.pullUp();
    }

    @Label("File Upload Demo")
    public ActionResult index() {
        return null;
    }
}
