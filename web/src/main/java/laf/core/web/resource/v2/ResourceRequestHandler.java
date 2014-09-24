package laf.core.web.resource.v2;

import java.io.*;
import java.util.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.core.http.CoreRequestInfo;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParseResult;
import laf.core.requestParserChain.RequestParser;

import org.fusesource.hawtbuf.ByteArrayInputStream;

import com.google.common.io.ByteStreams;

public class ResourceRequestHandler implements RequestParser<HttpRequest> {

	@Inject
	CoreRequestInfo coreRequestInfo;

	private Map<String, Resource> resources = new HashMap<>();

	private final Map<String, String> contentTypeMap = new HashMap<>();

	public ResourceRequestHandler() {
		contentTypeMap.put(".js", "application/javascript; ; charset=UTF-8");
		contentTypeMap.put(".css", "text/css; ; charset=UTF-8");
	}

	public void initialize(ResourceBundle... bundles) {
		initialize(Arrays.asList(bundles));
	}

	public void initialize(List<ResourceBundle> bundles) {
		for (ResourceBundle bundle : bundles) {
			for (ResourceOutput output : bundle.getResourceOutputs()) {
				output.getResources().forEach(
						r -> resources.put(r.getName(), r));
			}
		}
	}

	@Override
	public RequestParseResult<HttpRequest> parse(HttpRequest request) {
		String path = request.getPath();
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
