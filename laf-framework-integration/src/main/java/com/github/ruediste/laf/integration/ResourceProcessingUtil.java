package com.github.ruediste.laf.integration;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;

public class ResourceProcessingUtil {

	public static String process(String input,
			BiConsumer<Reader, Writer> processor) {
		StringWriter out = new StringWriter();
		processor.accept(new StringReader(input), out);
		return out.toString();
	}

	public static ResourceProcessing getProcessing() {
		IntegrationClassLoader cl = new IntegrationClassLoader(Thread
				.currentThread().getContextClassLoader(),
				ResourceProcessingImpl.class);
		try {
			return (ResourceProcessing) cl.loadClass(
					ResourceProcessingImpl.class.getName()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
