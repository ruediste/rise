package laf.core.web.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.attachedProperties.AttachedPropertyBearer;

import com.google.common.hash.Hashing;

/**
 * Represents a group of resources
 */
public class ResourceGroup {
	public final List<Resource> resources;
	public final StaticWebResourceBundle bundle;

	public ResourceGroup(StaticWebResourceBundle bundle, List<Resource> resources) {
		this.bundle = bundle;
		this.resources = resources;
	}

	public ResourceGroup(StaticWebResourceBundle bundle, Stream<Resource> resources) {
		this(bundle, resources.collect(Collectors.toList()));
	}

	public ResourceGroup process(Function<Resource, Resource> processor) {
		return new ResourceGroup(bundle, resources.stream().map(processor)
				.collect(Collectors.toList()));
	}

	public void send(Consumer<Resource> consumer) {
		resources.forEach(consumer);
	}

	@SafeVarargs
	final public ResourceGroup fork(final Consumer<ResourceGroup>... consumers) {
		for (Consumer<ResourceGroup> consumer : consumers) {
			consumer.accept(this);
		}

		return this;
	}

	public ResourceGroup filter(ResourceMode mode) {
		if (bundle.getMode() == mode) {
			return this;
		} else {
			return new ResourceGroup(bundle, Collections.emptyList());
		}

	}

	public ResourceGroup prod() {
		return filter(ResourceMode.PRODUCTION);
	}

	public ResourceGroup dev() {
		return filter(ResourceMode.DEVELOPMENT);
	}

	public ResourceGroup merge(ResourceGroup other) {
		ArrayList<Resource> list = new ArrayList<>();
		list.addAll(resources);
		list.addAll(other.resources);
		return new ResourceGroup(bundle, list);
	}

	public ResourceGroup collect(String nameTemplate) {

		return new ResourceGroup(bundle,
				Collections.singletonList(new CollectResource(resources)))
				.name(nameTemplate);
	}

	public ResourceGroup name(String template) {
		Pattern p = Pattern.compile("(\\A|[^\\\\])\\{hash\\}");
		Matcher m = p.matcher(template);
		boolean usesHash = m.find();

		// delegate to a caching resource to avoid retrieving the
		// data multiple times if hashing is used
		ResourceGroup underlying = usesHash ? cache() : this;

		ResourceGroup result = new ResourceGroup(bundle, underlying.resources
				.stream().map(x -> new DelegatingResource(x) {

					@Override
					public String getName() {
						return resolveNameTemplate(x, template);
					}
				}));

		// cache again to avoid calculating the name multiple time
		// when hashing is used
		return usesHash ? result.cache() : result;
	}

	String resolveNameTemplate(Resource resource, String template) {
		String name = resource.getName();
		Pattern p = Pattern
				.compile("(\\A|[^\\\\])\\{(?<placeholder>[^\\}]*)\\}");
		Matcher m = p.matcher(template);
		StringBuilder sb = new StringBuilder();
		int lastEnd = 0;
		while (m.find()) {
			sb.append(template.substring(lastEnd,
					m.start() == 0 ? 0 : m.start() + 1));
			lastEnd = m.end();
			String placeholder = m.group("placeholder");
			switch (placeholder) {
			case "hash":
				sb.append(Hashing.sha256().hashBytes(resource.getData())
						.toString());
				break;
			case "name": {
				String[] parts = name.split("/");
				parts = parts[parts.length - 1].split("\\.");

				sb.append(Arrays.asList(parts)
						.subList(0, parts.length == 1 ? 1 : parts.length - 1)
						.stream().collect(Collectors.joining(".")));
			}
				break;
			case "qname": {
				String[] parts = name.split("\\.");

				sb.append(Arrays.asList(parts)
						.subList(0, parts.length == 1 ? 1 : parts.length - 1)
						.stream().collect(Collectors.joining(".")));
			}
				break;
			case "ext": {
				String[] parts = name.split("\\.");
				sb.append(parts[parts.length - 1]);
			}
				break;
			default:
				throw new RuntimeException("Unknown placeholder " + placeholder
						+ " in name template " + template);
			}
		}
		sb.append(template.substring(lastEnd, template.length()));
		return sb.toString().replace("\\{", "{").replace("\\\\", "\\");
	}

	public ResourceGroup filter(Predicate<String> predicate) {
		return new ResourceGroup(bundle, resources.stream().filter(
				r -> predicate.test(r.getName())));
	}

	/**
	 * Filter by file extension
	 *
	 * @param extension
	 *            extension to filter for, without leading period. Example: "js"
	 */
	public ResourceGroup filter(String extension) {
		return filter(name -> name.endsWith("." + extension));
	}

	public ResourceGroup cache() {
		return new ResourceGroup(bundle, resources.stream().<Resource> map(
				r -> new CachingResource(r)));
	}

	private final static class CollectResource implements Resource,
			DataEqualityTracker {
		private List<Resource> resources;

		public CollectResource(List<Resource> resources) {
			this.resources = resources;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public byte[] getData() {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				for (Resource res : resources) {
					baos.write(res.getData());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return baos.toByteArray();
		}

		@Override
		public DataEqualityTracker getDataEqualityTracker() {
			return this;
		}

		@Override
		public boolean containsSameDataAs(DataEqualityTracker other) {
			if (getClass() != other.getClass()) {
				return false;
			}
			CollectResource o = (CollectResource) other;

			if (resources.size() != o.resources.size()) {
				return false;
			}
			Iterator<Resource> it = resources.iterator();
			Iterator<Resource> oit = o.resources.iterator();
			while (it.hasNext()) {
				if (!it.next()
						.getDataEqualityTracker()
						.containsSameDataAs(oit.next().getDataEqualityTracker())) {
					return false;
				}
			}
			return true;
		}
	}

	private final class CachingResource implements Resource {
		private Resource delegate;
		AttachedProperty<AttachedPropertyBearer, String> nameCacheProperty = new AttachedProperty<>();
		AttachedProperty<AttachedPropertyBearer, byte[]> dataCacheProperty = new AttachedProperty<>();

		public CachingResource(Resource delegate) {
			this.delegate = delegate;
		}

		@Override
		public String getName() {
			return nameCacheProperty.setIfAbsent(bundle.getResourceCache(),
					() -> delegate.getName());
		}

		@Override
		public byte[] getData() {
			return dataCacheProperty.setIfAbsent(bundle.getResourceCache(),
					() -> delegate.getData());
		}

		@Override
		public DataEqualityTracker getDataEqualityTracker() {
			return delegate.getDataEqualityTracker();
		}

	}
}
