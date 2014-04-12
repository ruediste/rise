package laf.module.model;

import java.util.*;

/**
 * Represents a project, consisting of {@link ModuleModel}s and
 * {@link ClassModel}s.
 *
 */
public class ProjectModel {

	/**
	 * Classes by {@link ClassModel#qualifiedName}
	 */
	private final HashMap<String, ClassModel> classes = new HashMap<>();

	/**
	 * Modules by {@link ModuleModel#qualifiedNameOfRepresentingClass}
	 */
	final private HashMap<String, ModuleModel> modules = new HashMap<>();

	public Map<String, ClassModel> getClasses() {
		return Collections.unmodifiableMap(classes);
	}

	public ClassModel getClassModel(String qualifiedName) {
		return classes.get(qualifiedName);
	}

	public Map<String, ModuleModel> getModules() {
		return Collections.unmodifiableMap(modules);
	}

	void addClass(ClassModel clazz) {
		classes.put(clazz.getQualifiedName(), clazz);
	}

	void addModule(ModuleModel module) {
		modules.put(module.getQualifiedNameOfRepresentingClass(), module);
	}

	/**
	 * Resolve class names recorded during parsing to the actual model objects.
	 */
	public void resolveDependencies() {
		for (ClassModel classModel : classes.values()) {
			classModel.resolveDependencies();
		}

		for (ModuleModel moduleModel : modules.values()) {
			moduleModel.resolveDependencies();
		}
	}

}
