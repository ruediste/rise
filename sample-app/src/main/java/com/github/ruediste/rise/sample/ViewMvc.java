package com.github.ruediste.rise.sample;

import java.io.IOException;

import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rise.api.ViewMvcBase;
import com.github.ruediste.rise.mvc.IControllerMvc;

public abstract class ViewMvc<TController extends IControllerMvc, TData>
		extends ViewMvcBase<TController, TData> {

	@Override
	public void render(HtmlCanvasTarget htmlTarget) throws IOException {
		render(new SampleCanvas(htmlTarget));
	}

	protected abstract void render(SampleCanvas html);
}
