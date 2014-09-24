package laf.core.web.resource.v2;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import laf.core.base.attachedProperties.AttachedPropertyBearer;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

import com.google.common.io.ByteStreams;

/**
 *
 */
public class ResourceBundle {

	@Inject
	protected Processors processors;

	@Inject
	ServletContext servletContext;

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
						return toByteArray(getClass().getClassLoader()
								.getResourceAsStream(name));
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
						return toByteArray(servletContext
								.getResourceAsStream(name));
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

}
