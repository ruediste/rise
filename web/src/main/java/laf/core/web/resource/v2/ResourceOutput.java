package laf.core.web.resource.v2;

import java.util.*;
import java.util.function.Consumer;

public class ResourceOutput implements Consumer<Resource> {

	final private List<Resource> resources = new ArrayList<>();

	public ResourceOutput(ResourceBundle bundle) {
		bundle.registerOutput(this);
	}

	@Override
	public void accept(Resource t) {
		resources.add(t);
	}

	public List<Resource> getResources() {
		return Collections.unmodifiableList(resources);
	}

	public void forEach(Consumer<Resource> consumer) {
		resources.stream().forEach(consumer);
	}
}
