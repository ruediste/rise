package laf.core.web.resource.v2;

import java.io.*;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

import org.fusesource.hawtbuf.ByteArrayInputStream;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.CssMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

@ApplicationScoped
public class Processors {

	public Function<Resource, Resource> minifyJs() {
		return wrapProcessor(new JSMinProcessor());
	}

	public Function<Resource, Resource> minifyCss() {
		return wrapProcessor(new CssMinProcessor());
	}

	private Function<Resource, Resource> wrapProcessor(
			ResourcePostProcessor processor) {
		return input -> {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
				processor.process(new InputStreamReader(
						new ByteArrayInputStream(input.getData()), "UTF-8"),
						writer);
				writer.close();
				return new DelegatingResource(input) {
					@Override
					public byte[] getData() {
						return out.toByteArray();
					}
				};
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
	}
}
