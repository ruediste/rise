package laf.initialization;

public abstract class InitializerImpl implements Initializer {

	final private Class<?> componentClass;

	public InitializerImpl(Class<?> componentClass) {
		super();
		this.componentClass = componentClass;
	}

	@Override
	public Class<?> getRepresentingClass() {
		return componentClass;
	}

	@Override
	final public boolean isBefore(Initializer other) {
		return declaresIsBefore(other) || other.declaresIsAfter(this);
	}

	@Override
	final public boolean isAfter(Initializer other) {
		return declaresIsAfter(other) || other.declaresIsBefore(this);
	}
}
