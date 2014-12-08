package laf.component.core.beanutil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A possibly nested property of a bean.
 */
public class BeanProperty {

	private final Type startClass;
	private final List<PropertyDescriptor> path;

	private BeanProperty(Type startClass) {
		this.startClass = startClass;
		path = Collections.emptyList();
	}

	public BeanProperty(Type startClass, ArrayList<PropertyDescriptor> path) {
		this.startClass = startClass;
		this.path = path;
	}

	public static BeanProperty of(Type startClass) {
		return new BeanProperty(startClass);
	}

	public BeanProperty with(PropertyDescriptor desc) {
		ArrayList<PropertyDescriptor> newPath = new ArrayList<>(getPath());
		newPath.add(desc);
		return new BeanProperty(startClass, newPath);
	}

	public Type getStartClass() {
		return startClass;
	}

	public List<PropertyDescriptor> getPath() {
		return Collections.unmodifiableList(path);
	}

}