package laf.core.web.resource;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import laf.core.base.LafLogger;
import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;

import com.google.common.hash.Hashing;

public class ResourceRequestHandler implements Consumer<ResourceRequest> {

	@Inject
	HttpServletRequest servletRequest;

	@Inject
	LafLogger log;

	private Map<ResourceBundle, BundleEntry> bundles = new ConcurrentHashMap<>();
	private Map<String, BundleEntry> bundlesByKey = new ConcurrentHashMap<>();

	private Object lock = new Object();

	private String resourcePrefix;

	private static class BundleEntry {

		byte[] data;

		public String getKey() {
			return Hashing.sha256().hashBytes(data).toString();
		}

	}

	@Override
	public void accept(ResourceRequest request) {

	}

	/**
	 * Prepare a bundle for serving. In development mode, nothing is done. In
	 * production mode, the the resources are collected and concatenated.
	 */
	public void prepareBundle(ResourceBundle bundle, Consumer<String> linkWriter) {
		BundleEntry entry = bundles.get(bundle);
		if (entry == null) {
			synchronized (lock) {
				entry = bundles.get(bundle);
				if (entry == null) {
					entry = generateEntry(bundle);
					bundles.put(bundle, entry);
					bundlesByKey.put(entry.getKey(), entry);
				}
			}
		}
		linkWriter.accept("bundles/" + entry.getKey()
				+ bundle.getType().getExtension());
	}

	private BundleEntry generateEntry(ResourceBundle bundle) {
		// TODO Auto-generated method stub
		return null;
	}

	private void loadAndProcessResource(String resourceName, Writer out) {
		try (InputStream in = servletRequest.getServletContext()
				.getResourceAsStream("/" + resourcePrefix + resourceName)) {

			new YUICssCompressorProcessor().process(new InputStreamReader(in,
					"UTF-8"), out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}