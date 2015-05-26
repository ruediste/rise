package com.github.ruediste.rise.sample;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.mvc.IControllerMvc;

public abstract class ViewMvc<TController extends IControllerMvc, TData>
        extends ViewMvcBase<TController, TData> {

    @Inject
    Provider<SampleCanvas> canvasProvider;

    @Override
    public void render(HtmlCanvasTarget htmlTarget) throws IOException {
        SampleCanvas canvas = canvasProvider.get();
        canvas.initialize(htmlTarget);
        render(canvas);
    }

    protected abstract void render(SampleCanvas html);
}
