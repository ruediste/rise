package laf.core.web.resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;

import laf.core.base.Consumer2;
import laf.core.base.LafLogger;
import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;

public class ResourceRequestHandler implements RequestParser<HttpRequest> {

	private static final String BUNDLES_PREFIX = "bundles/";

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	LafLogger log;

	private Map<ResourceBundle, BundleEntry> bundles = new ConcurrentHashMap<>();
	private Map<String, BundleEntry> bundlesByKey = new ConcurrentHashMap<>();

	private Object lock = new Object();

	private final Map<ResourceType, Consumer2<Reader, Writer>> resourceHandlers = new HashMap<>();
	private final Map<ResourceType, Consumer2<Reader, Writer>> concatenatedResourceHandlers = new HashMap<>();

	private String filePathPrefix;
	private String requestPrefix;
	private boolean useBundles;

	public void initialize(String filePathPrefix, String requestPrefix,
			boolean useBundles) {
		this.filePathPrefix = filePathPrefix;
		this.requestPrefix = requestPrefix;
		this.useBundles = useBundles;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		if (request.getPath().startsWith(requestPrefix)) {
			return this::accept;
		}
		return null;
	}

	public void accept(HttpRequest request) {
		String path = request.getPath().substring(requestPrefix.length());
		ResourceType resourceType = ResourceType.fromExtension(path);

		String pathWithoutExtension = path.substring(0, path.length()
				- resourceType.getExtension().length());

		try {
			coreRequestInfo.getServletResponse().setContentType(
					resourceType.getContentType());
			ServletOutputStream out = coreRequestInfo.getServletResponse()
					.getOutputStream();

			if (path.startsWith(BUNDLES_PREFIX)) {
				// serve bundle
				String key = pathWithoutExtension.substring(BUNDLES_PREFIX
						.length());
				BundleEntry entry = bundlesByKey.get(key);
				ByteSource.wrap(entry.data).copyTo(out);
			} else {
				// serve resource directly, possibly processing it first
				OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
				boolean found = false;

				// try to load it from the different source types, breaking as
				// soon as one is found
				for (ResourceType t : resourceType.getSourceTypes()) {
					if (loadAndProcessResource(
							pathWithoutExtension + t.getExtension(), writer)) {
						found = true;
						break;
					}
				}
				if (!found) {
					throw new RuntimeException("resource not found: " + path);
				}
				writer.close();
			}
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Call the linkWriter with the links required to render the bundle
	 */
	public void render(ResourceBundle bundle, Consumer<String> linkWriter) {
		if (useBundles) {
			BundleEntry entry = bundles.get(bundle);
			if (entry == null) {
				synchronized (lock) {
					entry = bundles.get(bundle);
					if (entry == null) {
						entry = generateEntry(bundle);
						bundles.put(bundle, entry);
						bundlesByKey.put(entry.key, entry);
					}
				}
			}
			linkWriter.accept(requestPrefix + BUNDLES_PREFIX + entry.key
					+ bundle.getType().getExtension());
		} else {
			for (String resourceName : bundle.getResourceNames()) {
				ResourceType t = ResourceType.fromExtension(resourceName);
				String nameWithoutExtension = resourceName.substring(0,
						resourceName.length() - t.getExtension().length());
				linkWriter.accept(requestPrefix + nameWithoutExtension
						+ t.getFinalType().getExtension());
			}
		}
	}

	private BundleEntry generateEntry(ResourceBundle bundle) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// collect and concatenate resource
			Writer out = new BufferedWriter(new OutputStreamWriter(baos,
					"UTF-8"));
			for (String resourceName : bundle.getResourceNames()) {
				if (!loadAndProcessResource(resourceName, out)) {
					throw new RuntimeException("resource not found: "
							+ getResourceFilePath(resourceName));
				}
			}
			out.close();

			// process concatenated resources
			Consumer2<Reader, Writer> concatenatedResourceHandler = getConcatenatedResourceHandlers()
					.get(bundle.getType());
			if (concatenatedResourceHandler != null) {
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				baos = new ByteArrayOutputStream();
				out = new BufferedWriter(new OutputStreamWriter(baos, "UTF-8"));

				try {
					concatenatedResourceHandler.accept(new InputStreamReader(
							bais, "UTF-8"), out);
				} catch (Throwable t) {
					throw new RuntimeException(
							"Error while processing bundle ("
									+ bundle.getType() + ") "
									+ bundle.getResourceNames(), t);
				}
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BundleEntry result = new BundleEntry(baos.toByteArray());
		return result;
	}

	private boolean loadAndProcessResource(String resourceName, Writer out) {
		ResourceType resourceType = ResourceType.fromExtension(resourceName);
		try (InputStream in = getResourceAsStream(resourceName)) {
			if (in == null) {
				return false;
			}
			Consumer2<Reader, Writer> handler = getResourceHandlers().get(
					resourceType);
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			if (handler != null) {
				handler.accept(reader, out);
			} else {
				CharStreams.copy(reader, out);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private InputStream getResourceAsStream(String resourceName) {
		return coreRequestInfo.getServletContext().getResourceAsStream(
				getResourceFilePath(resourceName));
	}

	private String getResourceFilePath(String resourceName) {
		return "/" + filePathPrefix + resourceName;
	}

	public Map<ResourceType, Consumer2<Reader, Writer>> getResourceHandlers() {
		return resourceHandlers;
	}

	public Map<ResourceType, Consumer2<Reader, Writer>> getConcatenatedResourceHandlers() {
		return concatenatedResourceHandlers;
	}

	private static class BundleEntry {

		byte[] data;
		String key;

		public BundleEntry(byte[] data) {
			this.data = data;
			key = Hashing.sha256().hashBytes(data).toString();
		}

	}

}