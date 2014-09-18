package laf.core.web.resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import laf.core.base.Consumer2;
import laf.core.base.Pair;
import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

/**
 * Request Handler for Resource Requests
 *
 * <p>
 * To reduce page loading times, web applications concatenate multiple CSS and
 * JavaScript files to a single file and remove comments and whitespaces as far
 * as possible. This reduces the amount of data which has to be transferred and
 * the number of time consuming server requests.
 * </p>
 *
 * <p>
 * To reference resources from a web page, resources of the same type are
 * grouped in a {@link ResourceBundle}. The
 * {@link #render(ResourceBundle, Consumer)} method is then used to generate the
 * servlet paths necessary to retrieve the resources from the server.
 * </p>
 *
 * <p>
 * There are two {@link ResourceRequestHandler} implementations: the
 * {@link BundleResourceRequestHandler} concatenates the resources from a bundle
 * and serves them upon a single request. This is well suited for production.
 * The {@link IndividualResourceRequestHandler} serves the resources with one
 * request per resource. The intended use for this handler is during
 * development. For details, see the documentation of the individual resource
 * handlers.
 * </p>
 *
 * <p>
 * Resources are first looked for using
 * {@link ServletContext#getResourceAsStream(String)}. If not found there, the
 * resource is looked up from the classpath. Resources can be processed by
 * transformers before serving them. For this purpose, a resource type is
 * determined by the file extension for each resource and a target resource type
 * is specified for a resource bundle. A resource type is identified by the file
 * extension, for example "css" for CSS files. Transformers can be registered by
 * putting them into the map returned by {@link #getResourceTransformers()}.
 * They are looked up using the type of the resource and the
 * {@link ResourceBundle#getTargetType()}. In addition, the
 * {@link BundleResourceRequestHandler} allows the registration of transformers
 * to be applied for bundles by the bundle target type.
 * </p>
 *
 */

public abstract class ResourceRequestHandler implements
		RequestParser<HttpRequest> {

	@Inject
	CoreRequestInfo coreRequestInfo;

	protected String contextPathPrefix;
	protected String requestPrefix;
	private final Map<Pair<ResourceType, ResourceType>, Consumer2<Reader, Writer>> resourceTransformers = new HashMap<>();
	private final Map<ResourceType, String> contentTypeMap = new HashMap<>();

	public ResourceRequestHandler() {
		super();
	}

	public abstract void render(ResourceBundle bundle,
			Consumer<String> linkWriter);

	/**
	 * Initialize this instance
	 *
	 * @param contextPathPrefix
	 *            resources are loaded via
	 *            {@link ServletContext#getResourceAsStream(String)}. The file
	 *            path prefix is prepended to the requested path. It is thus the
	 *            subdirectory in the war archive under which the resources are
	 *            placed. No leading slash allowed, trailing slash required.
	 * @param requestPrefix
	 *            request path (url) prefix used to access resources. No leading
	 *            slash allowed, trailing slash required.
	 */
	protected void initialize(String contextPathPrefix, String requestPrefix) {
		this.contextPathPrefix = contextPathPrefix;
		this.requestPrefix = requestPrefix;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		if (request.getPath().startsWith(requestPrefix)) {
			return this::handle;
		}
		return null;
	}

	public abstract void handle(HttpRequest request);

	public Map<Pair<ResourceType, ResourceType>, Consumer2<Reader, Writer>> getResourceTransformers() {
		return resourceTransformers;
	}

	protected InputStream loadResource(String resourceName) {
		InputStream result;
		String contextFileName = "/" + contextPathPrefix + resourceName;
		result = coreRequestInfo.getServletContext().getResourceAsStream(
				contextFileName);
		if (result == null) {
			String classloaderResourceName = "/" + resourceName;
			getClass().getClassLoader().getResourceAsStream(
					classloaderResourceName);

			if (result == null) {
				throw new RuntimeException("Unable to find resource "
						+ resourceName + ", neither under " + contextFileName
						+ " in the servlet context nor under "
						+ classloaderResourceName + " by using the classloader");
			}
		}
		return result;
	}

	protected String url(String subPath) {
		return requestPrefix + subPath;
	}
}