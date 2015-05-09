package com.github.ruediste.rise.integration;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import ro.isdc.wro.model.resource.processor.impl.css.CssMinProcessor;

public class ResourceProcessingImpl implements ResourceProcessing {

	@Override
	public void minifyCss(Reader reader, Writer writer) {
		try {
			new CssMinProcessor().process(reader, writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
