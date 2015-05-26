package com.github.ruediste.rise.sample;

import java.io.Writer;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasBase;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;

public class SampleCanvas extends HtmlCanvasBase<SampleCanvas> implements
		BootstrapCanvas<SampleCanvas> {

	public SampleCanvas(Writer output) {
		super(output);
	}

	public SampleCanvas(HtmlCanvasTarget target) {
		super(target);
	}

	@Override
	public SampleCanvas self() {
		return this;
	}

}
