package com.github.ruediste.rise.testApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.mvc.IControllerMvc;

public abstract class ViewMvc<TController extends IControllerMvc, TData>
        extends ViewMvcBase<TController, TData> {

    @Inject
    Provider<TestCanvas> canvasProvider;

    @Override
    public void render(ByteArrayOutputStream stream) throws IOException {
        render(stream, canvasProvider.get(), this::render);
    }

    protected abstract void render(TestCanvas html);

}
