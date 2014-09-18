package laf.core.web.resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import laf.core.base.*;
import laf.core.http.CoreRequestInfo;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * {@link ResourceRequestHandler} concatenating the resources from a
 * {@link ResourceBundle} to a bundle and serving them by a single request.
 *
 * <p>
 * The source resources are loaded and transformed to
 * {@link ResourceBundle#getTargetType()}. Then the transformed resources are
 * concatenated. If a {@link #bundleResourceTransformers} is registered for the
 * bundle target type, the concatenated bundle is transformed. Finally the
 * bundle is hashed using SHA-256 and placed in a map. The generated servlet
 * path starts with the hash, followed by the extension from the target resource
 * type of the bundle.
 * </p>
 *
 * <p>
 * When a bundle is requested, the hash is extracted from the path, the
 * corresponding data is looked up and serve using the mime-type matching the
 * extension from the request.
 * </p>
 */
public class BundleResourceRequestHandler extends ResourceRequestHandler {

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	LafLogger log;

	private Map<ResourceBundle, String> bundles = new ConcurrentHashMap<>();
	private Map<String, byte[]> bundlesByKey = new ConcurrentHashMap<>();

	private Object lock = new Object();

	private final Map<ResourceType, Consumer2<Reader, Writer>> bundleResourceTransformers = new HashMap<>();

	@Override
	public void initialize(String contextPathPrefix, String servletPathPrefix) {
		super.initialize(contextPathPrefix, servletPathPrefix);
	}

	@Override
	public void handle(String path, HttpServletResponse response) {

		ResourceType resourceType = ResourceType.fromExtension(path);

		String hash = path.substring(0, path.length()
				- resourceType.getIdentifier().length() - 1);

		setContentType(resourceType, response);
		try (ServletOutputStream out = coreRequestInfo.getServletResponse()
				.getOutputStream();) {

			byte[] data = bundlesByKey.get(hash);
			ByteSource.wrap(data).copyTo(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Call the linkWriter with the links required to render the bundle
	 */
	@Override
	public void render(ResourceBundle bundle, Consumer<String> linkWriter) {
		String hash = bundles.get(bundle);
		if (!bundles.containsKey(bundle)) {
			synchronized (lock) {
				if (!bundles.containsKey(bundle)) {
					byte[] data = generateBundle(bundle);
					hash = Hashing.sha256().hashBytes(data).toString();
					bundles.put(bundle, hash);
					bundlesByKey.put(hash, data);
				}
			}
		}

		linkWriter.accept(servletPath(hash) + "."
				+ bundle.getTargetType().getIdentifier());
	}

	private byte[] generateBundle(ResourceBundle bundle) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// collect and concatenate resource
			for (String resourceName : bundle.getResourceNames()) {
				loadAndProcessResource(resourceName, bundle.getTargetType(),
						baos);
			}

			// transform concatenated resources
			Consumer2<Reader, Writer> bundleTransformer = bundleResourceTransformers
					.get(bundle.getTargetType());
			if (bundleTransformer != null) {
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				baos = new ByteArrayOutputStream();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						baos, "UTF-8"));

				try {
					bundleTransformer.accept(new InputStreamReader(bais,
							"UTF-8"), out);
				} catch (Throwable t) {
					throw new RuntimeException(
							"Error while processing bundle ("
									+ bundle.getTargetType() + ") "
									+ bundle.getResourceNames(), t);
				}
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	private void loadAndProcessResource(String resourceName,
			ResourceType targetType, OutputStream out) {
		ResourceType resourceType = ResourceType.fromExtension(resourceName);
		try (InputStream in = loadResource(resourceName)) {
			Consumer2<Reader, Writer> transformer = getResourceTransformers()
					.get(resourceType);
			if (transformer != null) {
				InputStreamReader reader = new InputStreamReader(in, "UTF-8");
				Writer writer = new BufferedWriter(new OutputStreamWriter(out,
						"UTF-8"));
				transformer.accept(reader, writer);
				writer.close();
				reader.close();
			} else {
				ByteStreams.copy(in, out);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<ResourceType, Consumer2<Reader, Writer>> getBundleResourceTransformers() {
		return bundleResourceTransformers;
	}

	public void addBundleTransformer(ResourceType type,
			ThrowingConsumer2<Reader, Writer> transformer) {
		bundleResourceTransformers
				.put(type, Consumer2.nonThrowing(transformer));
	}
}