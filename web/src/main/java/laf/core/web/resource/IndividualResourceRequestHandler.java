package laf.core.web.resource;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import laf.core.base.Consumer2;
import laf.core.base.Pair;
import laf.core.http.request.HttpRequest;

/**
 * {@link ResourceRequestHandler} serving resources using individual requests.
 *
 * <p>
 * The URLs for resources which do not need to be transformed are prefixed with
 * "original/", those which need to be transformed are prefixed with
 * "tranformed/". The extension used in the URL matches the
 * {@link ResourceBundle#getTargetType()}. The mapping from resource name to the
 * URL is remembered by the handler.
 * </p>
 *
 * <p>
 * When a request needs to be handled, the underlying resource is retrieved,
 * optionally transformed and then served.
 * </p>
 *
 */
public class IndividualResourceRequestHandler extends ResourceRequestHandler {

	private final ConcurrentMap<String, String> resourceMapping = new ConcurrentHashMap<>();

	@Override
	protected void initialize(String contextPathPrefix, String requestPrefix) {
		super.initialize(contextPathPrefix, requestPrefix);
	}

	@Override
	public void render(ResourceBundle bundle, Consumer<String> linkWriter) {
		for (String resource : bundle.getResourceNames()) {

			// test that the resource is present
			InputStream in = loadResource(resource);
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException();
			}

			ResourceType type = ResourceType.of(resource);
			Consumer2<Reader, Writer> transformer = getResourceTransformers()
					.get(Pair.of(ResourceType.of(resource),
							bundle.getTargetType()));

			// if there is no transformer, the resource type has to match the
			// bundle
			// target type
			if (transformer == null && !type.equals(bundle.getTargetType())) {
				throw new RuntimeException(
						"Resource type "
								+ type
								+ " of resource "
								+ resource
								+ " does not match bundle type "
								+ bundle.getTargetType()
								+ " and there is no transformer for that type combination registered. Available transformers: "
								+ getResourceTransformers()
										.keySet()
										.stream()
										.map(p -> p.getA().toString() + "->"
												+ p.getB())
										.collect(Collectors.joining(", ")));
			}

			// determine the webResource
			String webResource;
			if (type.equals(bundle.getTargetType())) {
				webResource = "orig/" + resource;
			} else {
				String resourceNoExtension = resource.substring(0,
						resource.length() - type.getIdentifier().length() - 1);
				webResource = "transformed/" + resourceNoExtension + "."
						+ bundle.getTargetType().getIdentifier();
			}

			resourceMapping.put(webResource, resource);
			linkWriter.accept(url(webResource));
		}

	}

	@Override
	public void handle(HttpRequest request) {
		// TODO Auto-generated method stub

	}

}
