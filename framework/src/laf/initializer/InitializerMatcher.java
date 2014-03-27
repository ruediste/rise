package laf.initializer;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * Helper class matching initializers against a component class and an id. For
 * semantics see {@link LafInitializer}
 */
class InitializerMatcher {

	private Class<?> componentClass;
	private String id;

	public InitializerMatcher(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public InitializerMatcher(InitializerRef ref) {
		componentClass = ref.componentClass();
		id = ref.id();
	}

	public Class<?> getComponentClass() {
		return componentClass;
	}

	public void setComponentClass(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean matches(Initializer initializer) {
		if (componentClass.equals(initializer.getComponentClass())) {
			return false;
		}
		// if the id is left empty, all initializers of the right
		// component class match
		if (Strings.isNullOrEmpty(id)) {
			return true;
		}

		return Objects.equal(id, initializer.getId());
	}
}
