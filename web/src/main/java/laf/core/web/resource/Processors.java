package laf.core.web.resource;

import java.io.*;
import java.util.Objects;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

import org.fusesource.hawtbuf.ByteArrayInputStream;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.CssMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

@ApplicationScoped
public class Processors {

	private static final class ProcessingResource extends DelegatingResource {

		private Resource delegate;
		private ResourcePostProcessor processor;
		private Object processorIdentifier;

		private ProcessingResource(ResourcePostProcessor processor,
				Object processorIdentifier, Resource delegate) {
			super(delegate);
			this.processor = processor;
			this.processorIdentifier = processorIdentifier;
			this.delegate = delegate;
		}

		@Override
		public byte[] getData() {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
				processor.process(new InputStreamReader(
						new ByteArrayInputStream(delegate.getData()), "UTF-8"),
						writer);
				writer.close();
				return out.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public DataEqualityTracker getDataEqualityTracker() {
			return new ProcessingResourceDataEqualityTracker(delegate,
					processorIdentifier);
		}

	}

	private static final class ProcessingResourceDataEqualityTracker implements
			DataEqualityTracker {

		private Resource delegate;
		private Object processorIdentifier;

		public ProcessingResourceDataEqualityTracker(Resource delegate,
				Object processorIdentifier) {
			this.delegate = delegate;
			this.processorIdentifier = processorIdentifier;
		}

		@Override
		public boolean containsSameDataAs(DataEqualityTracker other) {
			if (getClass() != other.getClass()) {
				return false;
			}
			ProcessingResourceDataEqualityTracker o = (ProcessingResourceDataEqualityTracker) other;
			if (!Objects.equals(processorIdentifier, o.processorIdentifier)) {
				return false;
			}

			return delegate.getDataEqualityTracker().containsSameDataAs(
					o.delegate.getDataEqualityTracker());
		}
	}

	public Function<Resource, Resource> minifyJs() {
		return wrapProcessor(new JSMinProcessor(), JSMinProcessor.class);
	}

	public Function<Resource, Resource> minifyCss() {
		return wrapProcessor(new CssMinProcessor(), CssMinProcessor.class);
	}

	Function<Resource, Resource> wrapProcessor(ResourcePostProcessor processor,
			Object processorIdentifier) {
		return input -> new ProcessingResource(processor, processorIdentifier,
				input);
	}
}
