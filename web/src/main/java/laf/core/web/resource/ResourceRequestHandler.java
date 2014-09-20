package laf.core.web.resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.core.base.*;
import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

/**
 * Request Handler for Resource Requests
 *
 * <p>
 * Most HTML pages use various CSS and JavaScript resources. After loading a
 * html page, the browser loads all referenced resources to complete page
 * loading.
 * </p>
 *
 * </p> To reduce page loading times, it is preferable to concatenate multiple
 * CSS and JavaScript resources and remove comments and whitespace as far as
 * possible. This reduces the amount of data which has to be transferred and the
 * number of time consuming server requests. In addition, the source of a CSS or
 * JavaScript resource is written in a different language which is transformed
 * or compiled. </p>
 *
 * <p>
 * These transformations can either performed at build time or at run time.
 * Runtime transformation was chosen since it removes the need of maintaining a
 * complex build system.
 * </p>
 *
 * <p>
 * To reference resources in a web page, resources which are transformed to the
 * same resource type are grouped in a {@link ResourceBundle}. The
 * {@link #render(ResourceBundle, Consumer)} method is then used to generate the
 * servlet paths to be used to reference the resources in the web page. When the
 * servlet paths are requested, the {@link ResourceRequestHandler} will serve
 * the transformed resources from the bundle.
 * </p>
 *
 * <p>
 * There are two {@link ResourceRequestHandler} implementations: the
 * {@link BundleResourceRequestHandler} concatenates the resources from a bundle
 * and serves them upon a single request. This is well suited for production.
 * The {@link IndividualResourceRequestHandler} serves the resources
 * individually. The intended use for this handler is during development. For
 * details, see the documentation of the individual resource handlers.
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
	protected String servletPathPrefix;
	private final Map<Pair<ResourceType, ResourceType>, Consumer2<Reader, Writer>> resourceTransformers = new HashMap<>();
	private final Map<ResourceType, String> contentTypeMap = new HashMap<>();

	public ResourceRequestHandler() {
		super();
		contentTypeMap.put(ResourceType.JS,
				"application/javascript; ; charset=UTF-8");
		contentTypeMap.put(ResourceType.CSS, "text/css; ; charset=UTF-8");
	}

	public abstract void render(ResourceBundle bundle,
			Consumer<String> servletPathConsumer);

	/**
	 * Initialize this instance
	 *
	 * @param contextPathPrefix
	 *            resources are loaded via
	 *            {@link ServletContext#getResourceAsStream(String)}. The file
	 *            path prefix is prepended to the requested path. It is thus the
	 *            subdirectory in the war archive under which the resources are
	 *            placed. No leading slash allowed, trailing slash required.
	 * @param servletPathPrefix
	 *            {@link HttpServletRequest#getServletPath()} prefix used to
	 *            access resources. No leading slash allowed, trailing slash
	 *            required.
	 */
	protected void initialize(String contextPathPrefix, String servletPathPrefix) {
		this.contextPathPrefix = contextPathPrefix;
		this.servletPathPrefix = servletPathPrefix;
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		if (request.getPath().startsWith(servletPathPrefix)) {
			return x -> handle(x.getPath()
					.substring(servletPathPrefix.length()),
					coreRequestInfo.getServletResponse());
		}
		return null;
	}

	public abstract void handle(String path, HttpServletResponse response);

	public Map<Pair<ResourceType, ResourceType>, Consumer2<Reader, Writer>> getResourceTransformers() {
		return resourceTransformers;
	}

	public void addResourceTransformer(ResourceType sourceType,
			ResourceType targetType,
			ThrowingConsumer2<Reader, Writer> transformer) {
		resourceTransformers.put(Pair.of(sourceType, targetType),
				Consumer2.nonThrowing(transformer));
	}

	protected InputStream loadResource(String resourceName) {
		InputStream result;
		String contextFileName = "/" + contextPathPrefix + resourceName;
		result = coreRequestInfo.getServletContext().getResourceAsStream(
				contextFileName);
		if (result == null) {
			String classloaderResourceName = "" + resourceName;
			result = getClass().getClassLoader().getResourceAsStream(
					classloaderResourceName);

			if (result == null) {
				throw new RuntimeException("Unable to find resource "
						+ resourceName + ", neither under " + contextFileName
						+ " in the servlet context nor under "
						+ classloaderResourceName + " using the classloader");
			}
		}
		return result;
	}

	protected String servletPath(String subPath) {
		return servletPathPrefix + subPath;
	}

	public Map<ResourceType, String> getContentTypeMap() {
		return contentTypeMap;
	}

	protected void setContentType(ResourceType resourceType,
			HttpServletResponse response) {
		{
			String contentType = getContentTypeMap().get(resourceType);
			if (contentType != null) {
				response.setContentType(contentType);
			}
		}
	}
}