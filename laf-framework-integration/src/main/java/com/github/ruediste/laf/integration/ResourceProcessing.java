package com.github.ruediste.laf.integration;

import java.io.Reader;
import java.io.Writer;

public interface ResourceProcessing {
	void minifyCss(Reader reader, Writer writer);
}
