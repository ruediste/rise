package laf.core.web.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.core.base.attachedProperties.AttachedPropertyBearer;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;
import laf.core.http.CoreRequestInfo;

import com.google.common.io.ByteStreams;

/**
 *
 */
@ApplicationScoped
public abstract class StaticWebResourceBundle {

	@Inject
	protected Processors processors;

	@Inject
	CoreRequestInfo info;

	private final List<ResourceOutput> resourceOutputs = new ArrayList<>();

	private volatile AttachedPropertyBearer resourceCache = new AttachedPropertyBearerBase();

	private ResourceMode mode;

	public Function<PathGroup, ResourceGroup> classPath() {
		return group -> new ResourceGroup(this, group.getPaths().stream()
				.<Resource> map(name -> new Resource() {

					@Override
					public String getName() {
						return name;
					}

					@Override
					public byte[] getData() {
						InputStream in = getClass().getClassLoader()
								.getResourceAsStream(
										name.startsWith("/") ? name
												.substring(1) : name);
						if (in == null) {
							throw new RuntimeException(
									"Unable to find resource on classpath: "
											+ name);
						}
						return toByteArray(in);
					}

					@Override
					public DataEqualityTracker getDataEqualityTracker() {
						return new DataEqualityTrackerImpl(name);
					};

					final class DataEqualityTrackerImpl implements
							DataEqualityTracker {

						private String name;

						public DataEqualityTrackerImpl(String name) {
							this.name = name;
						}

						@Override
						public boolean containsSameDataAs(
								DataEqualityTracker other) {
							if (getClass() != other.getClass()) {
								return false;
							}
							DataEqualityTrackerImpl o = (DataEqualityTrackerImpl) other;
							return name.equals(o.name);
						}

					}
				}));
	}

	public Function<PathGroup, ResourceGroup> servletContext() {
		return group -> new ResourceGroup(this, group.getPaths().stream()
				.<Resource> map(name -> new Resource() {

					@Override
					public String getName() {
						return name;
					}

					@Override
					public byte[] getData() {
						InputStream in = info.getServletContext()
								.getResourceAsStream(name);
						if (in == null) {
							throw new RuntimeException(
									"Unable to find resource in servlet context: "
											+ name);
						}
						return toByteArray(in);
					}

					@Override
					public DataEqualityTracker getDataEqualityTracker() {
						return new DataEqualityTrackerImpl(name);
					}

					final class DataEqualityTrackerImpl implements
							DataEqualityTracker {

						private String name;

						public DataEqualityTrackerImpl(String name) {
							this.name = name;
						}

						@Override
						public boolean containsSameDataAs(
								DataEqualityTracker other) {
							if (getClass() != other.getClass()) {
								return false;
							}
							DataEqualityTrackerImpl o = (DataEqualityTrackerImpl) other;
							return name.equals(o.name);
						}

					}

				}));
	}

	public PathGroup paths(String... paths) {
		return new PathGroup(this, Arrays.asList(paths));
	}

	private byte[] toByteArray(InputStream in) {
		try {
			return ByteStreams.toByteArray(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean dev() {
		return mode == ResourceMode.DEVELOPMENT;
	}

	public boolean prod() {
		return mode == ResourceMode.PRODUCTION;
	}

	public ResourceMode getMode() {
		return mode;
	}

	public void setMode(ResourceMode mode) {
		this.mode = mode;
	}

	public void registerOutput(ResourceOutput resourceOutput) {
		resourceOutputs.add(resourceOutput);
	}

	public AttachedPropertyBearer getResourceCache() {
		return resourceCache;
	}

	public void clearCache() {
		resourceCache = new AttachedPropertyBearerBase();
	}

	public List<ResourceOutput> getResourceOutputs() {
		return Collections.unmodifiableList(resourceOutputs);
	}

	public void initialize(ResourceMode mode) {
		this.mode = mode;
		initializeImpl();
	}

	protected abstract void initializeImpl();
}
