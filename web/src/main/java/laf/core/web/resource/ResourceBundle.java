package laf.core.web.resource;

import java.util.*;

public class ResourceBundle {

	final private ResourceType type;
	final private List<String> resources;

	public ResourceBundle(ResourceType type, String... resources) {
		this.type = type;
		this.resources = Arrays.asList(resources);
	}

	public ResourceType getType() {
		return type;
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
		return type == other.type && resources.equals(other.resources);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, resources);
	}
}
