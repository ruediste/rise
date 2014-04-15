package laf.initialization;


/**
 * Helper class matching initializers against a component class and an id. For
 * semantics see {@link LafInitializer}
 */
class InitializerMatcher {

	private Class<?> componentClass;

	public InitializerMatcher(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public Class<?> getComponentClass() {
		return componentClass;
	}

	public void setComponentClass(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public boolean matches(Initializer initializer) {
		return componentClass.isAssignableFrom(initializer
				.getRepresentingClass());
	}
}
