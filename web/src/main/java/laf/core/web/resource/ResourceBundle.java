package laf.core.web.resource;

import java.util.*;

/**
 * Immutable collection of resources
 */
public class ResourceBundle {

	final private ResourceType targetType;
	final private List<String> resources;
	final private int hashCode;

	/**
	 * Create a new resource bundle. The provided resources should be
	 * transformed to the given target type.
	 */
	public ResourceBundle(ResourceType type, String... resources) {
		targetType = type;
		this.resources = Arrays.asList(resources);
		hashCode = Objects.hash(type, resources);
	}

	public ResourceType getTargetType() {
		return targetType;
	}

	public List<String> getResourceNames() {
		return Collections.unmodifiableList(resources);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		ResourceBundle other = (ResourceBundle) obj;
		return targetType == other.targetType
				&& resources.equals(other.resources);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
