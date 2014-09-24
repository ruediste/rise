package laf.core.web.resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import laf.core.base.Consumer2;
import laf.core.base.Pair;

import com.google.common.io.ByteStreams;

/**
 * {@link ResourceRequestHandler} serving resources using individually.
 *
 * <p>
 * The URLs for resources which do not need to be transformed are prefixed with
 * "original/", those which need to be transformed are prefixed with
 * "tranformed/". The extension used in the URL matches the
 * {@link ResourceBundle#getTargetType()}. The mapping from source resource to
 * the transformed resource is remembered by the handler.
 * </p>
 *
 * <p>
 * When a request needs to be handled, the source resource is retrieved,
 * optionally transformed and then served.
 * </p>
 *
 */
public class IndividualResourceRequestHandler extends ResourceRequestHandler {

	private final Object lock = new Object();
	private final Map<String, String> resourceMapping = new HashMap<>();

	@Override
	public void initialize(String contextPathPrefix, String requestPrefix) {
		super.initialize(contextPathPrefix, requestPrefix);
	}

	@Override
	public void render(ResourceBundle bundle, Consumer<String> linkWriter) {
		for (String sourceName : bundle.getResourceNames()) {

			// test that the source is present
			InputStream in = loadResource(sourceName);
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException();
			}

			ResourceType sourceType = ResourceType.fromExtension(sourceName);
			Consumer2<Reader, Writer> transformer = getResourceTransformers()
					.get(Pair.of(ResourceType.fromExtension(sourceName),
							bundle.getTargetType()));

			// if there is no transformer, the resource type has to match the
			// bundle
			// target type
			if (transformer == null
					&& !sourceType.equals(bundle.getTargetType())) {
				throw new RuntimeException(
						"Resource type "
								+ sourceType
								+ " of resource "
								+ sourceName
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

			// determine the transformed resource name
			String targetName;
			if (transformer == null) {
				targetName = "orig/" + sourceName;
			} else {
				String resourceNoExtension = sourceName.substring(0,
						sourceName.length()
								- sourceType.getIdentifier().length() - 1);
				targetName = "transformed/" + resourceNoExtension + "."
						+ bundle.getTargetType().getIdentifier();
			}

			synchronized (lock) {
				String existingSource = resourceMapping.get(targetName);
				if (existingSource == null) {
					resourceMapping.put(targetName, sourceName);
				} else {
					if (!sourceName.equals(existingSource)) {
						throw new RuntimeException("The resource " + targetName
								+ ", mapped to from " + sourceName
								+ " has already been used for "
								+ existingSource);
					}
				}
			}
			linkWriter.accept(servletPath(targetName));
		}

	}

	@Override
	public void handle(String path, HttpServletResponse response) {
		String source = resourceMapping.get(path);
		ResourceType targetType = ResourceType.fromExtension(path);
		ResourceType sourceType = ResourceType.fromExtension(source);
		Consumer2<Reader, Writer> transformer = getResourceTransformers().get(
				Pair.of(sourceType, targetType));

		try {
			InputStream in = loadResource(source);

			{
				String contentType = getContentTypeMap().get(sourceType);
				if (contentType != null) {
					response.setContentType(contentType);
				}
			}

			ServletOutputStream out = response.getOutputStream();

			if (transformer != null) {
				InputStreamReader reader = new InputStreamReader(in, "UTF-8");
				OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
				transformer.accept(reader, writer);
				reader.close();
				writer.close();
			} else {
				ByteStreams.copy(in, out);
			}

			in.close();
			out.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
