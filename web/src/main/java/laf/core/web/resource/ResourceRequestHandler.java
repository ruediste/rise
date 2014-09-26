package laf.core.web.resource;

import java.io.*;
import java.util.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

import org.slf4j.Logger;

import com.google.common.io.ByteStreams;

public class ResourceRequestHandler implements RequestParser<HttpRequest> {

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	Logger log;

	private Map<String, Resource> resources = new HashMap<>();
	private Map<String, ResourceBundle> resourceToBundle = new HashMap<>();

	private final Map<String, String> contentTypeMap = new HashMap<>();

	public ResourceRequestHandler() {
		contentTypeMap.put(".js", "application/javascript; ; charset=UTF-8");
		contentTypeMap.put(".css", "text/css; ; charset=UTF-8");
	}

	public void initialize(ResourceMode mode, ResourceBundle... bundles) {
		initialize(mode, Arrays.asList(bundles));
	}

	public void initialize(ResourceMode mode, List<ResourceBundle> bundles) {
		for (ResourceBundle bundle : bundles) {
			bundle.initialize(mode);
			log.info("registering resources of bundle "
					+ bundle.getClass().getName());
			for (ResourceOutput output : bundle.getResourceOutputs()) {
				output.getResources()
						.forEach(
								r -> {
									String name = r.getName();

									Resource existing = resources.get(name);
									if (existing != null
											&& !existing.containsSameDataAs(r)) {
										throw new RuntimeException(
												"Conflict while registring resource "
														+ name
														+ ": Existing resource from bundle "
														+ resourceToBundle.get(
																name)
																.getClass()
														+ " registered a different\ncontent than the resource registered by bundle "
														+ bundle.getClass()
																.getName());
									}

									log.info("registering resource " + name);
									resources.put(name, r);
									resourceToBundle.put(name, bundle);
								});
			}
		}
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		String path = "/" + request.getPath();
		Resource resource = resources.get(path);
		if (resource == null) {
			return null;
		} else {
			return new ParseResult(path, resource);
		}
	}

	public Map<String, String> getContentTypeMap() {
		return contentTypeMap;
	}

	public void putContentType(String extension, String contentType) {
		contentTypeMap.put(extension, contentType);
	}

	public class ParseResult implements RequestParseResult<HttpRequest> {

		private Resource resource;
		private String path;

		public ParseResult(String path, Resource resource) {
			this.path = path;
			this.resource = resource;
		}

		@Override
		public void handle(HttpRequest request) {
			String extension;
			{
				String[] parts = path.split("\\.");
				extension = "." + parts[parts.length - 1];
			}

			HttpServletResponse response = coreRequestInfo.getServletResponse();

			try (InputStream in = new ByteArrayInputStream(resource.getData());
					OutputStream out = response.getOutputStream();) {

				{
					String contentType = contentTypeMap.get(extension);
					if (contentType != null) {
						response.setContentType(contentType);
					}
				}

				ByteStreams.copy(in, out);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
