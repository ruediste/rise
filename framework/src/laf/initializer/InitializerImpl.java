package laf.initializer;

public abstract class InitializerImpl implements Initializer {

	final private String id;
	final private Class<?> componentClass;

	public InitializerImpl(Class<?> componentClass, String id) {
		super();
		this.componentClass = componentClass;
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<?> getComponentClass() {
		return componentClass;
	}
}
