package com.github.ruediste.laf.core.web.assetPipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.util.AsmUtil;
import com.github.ruediste.laf.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class AssetRequestMapper {

	private final static class AssetRequestParseResult implements
			RequestParseResult {
		@Inject
		CoreRequestInfo info;

		@Inject
		Logger log;

		private Asset asset;

		AssetRequestParseResult(Asset asset) {
			this.asset = asset;

		}

		@Override
		public void handle() {
			HttpServletResponse response = info.getServletResponse();
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(asset.getContentType());
			byte[] data = asset.getData();
			response.setContentLength(data.length);
			try (OutputStream out = response.getOutputStream()) {
				out.write(data, 0, data.length);
			} catch (IOException e) {
				log.warn("Error while sending asset response", e);
			}
		}
	}

	@Inject
	ClassHierarchyCache cache;

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	AssetPipelineConfiguration pipelineConfiguration;

	@Inject
	Injector injector;

	@Inject
	PathInfoIndex index;

	private List<AssetBundle> bundles = new ArrayList<>();

	public void initialize() {
		String internalName = Type.getInternalName(AssetBundle.class);
		registerBundles(internalName);
		registerAssets(bundles);
	}

	void registerAssets(List<AssetBundle> bundles) {
		Multimap<String, Pair<AssetBundle, Asset>> assets = ArrayListMultimap
				.create();

		for (AssetBundle bundle : bundles) {
			for (AssetBundleOutput output : bundle.outputs) {
				for (Asset asset : output.getAssets()) {
					String pathInfo = pipelineConfiguration.assetPathInfoPrefix
							+ asset.getName();
					assets.put(pathInfo, Pair.of(bundle, asset));

				}
			}
		}

		for (Entry<String, Collection<Pair<AssetBundle, Asset>>> entry : assets
				.asMap().entrySet()) {
			if (entry.getValue().size() > 1) {
				// there are multiple assets for a single path. Make sure they
				// contain the same data
				HashFunction goodFastHash = Hashing.goodFastHash(128);
				HashMap<HashCode, Pair<AssetBundle, Asset>> map = new HashMap<>();
				for (Pair<AssetBundle, Asset> pair : entry.getValue()) {
					Pair<AssetBundle, Asset> existing = map
							.put(goodFastHash.hashBytes(pair.getB().getData()),
									pair);
					if (existing != null) {
						throw new RuntimeException(
								"Two Assets map to the same name "
										+ entry.getKey()
										+ " but contain different data. They are declared in the following bundles:\n"
										+ pair.getA().getClass().getName()
										+ "\n"
										+ existing.getA().getClass().getName());
					}
				}
			}

			// all assets mapping to this path have the same data. Just pick the
			// first
			index.registerPathInfo(
					entry.getKey(),
					x -> new AssetRequestParseResult(Iterables.getFirst(
							entry.getValue(), null).getB()));
		}
	}

	void registerBundles(String internalName) {
		for (ClassNode child : cache.getChildren(internalName)) {
			registerBundle(child);
			registerBundles(child.name);
		}
	}

	void registerBundle(ClassNode cls) {
		Class<?> bundleClass;
		try {
			bundleClass = AsmUtil.loadClass(Type.getObjectType(cls.name),
					coreConfiguration.dynamicClassLoader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Unable to load AssetBundle " + cls.name, e);
		}
		AssetBundle bundle = (AssetBundle) injector.getInstance(bundleClass);
		bundles.add(bundle);
	}
}
