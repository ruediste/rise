package laf.module.model;

import java.util.*;

public class ClassModel {

	private final ProjectModel projectModel;
	private final String qualifiedName;

	public ClassModel(ProjectModel projectModel, String qualifiedName) {
		this.projectModel = projectModel;
		this.qualifiedName = qualifiedName;
		projectModel.addClass(this);
	}

	ModuleModel module;

	final Set<ClassModel> usesClasses = new HashSet<>();
	final Set<ClassModel> usedByClasses = new HashSet<>();

	final Set<String> usesClassNames = new HashSet<>();

	public String getQualifiedName() {
		return qualifiedName;
	}

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public Set<ClassModel> getUsesClasses() {
		return Collections.unmodifiableSet(usesClasses);
	}

	public void addUsesClass(ClassModel clazz) {
		usesClasses.add(clazz);
		clazz.usedByClasses.add(this);
	}

	public Set<ClassModel> getUsedByClasses() {
		return Collections.unmodifiableSet(usedByClasses);
	}

	public Set<String> getUsesClassNames() {
		return Collections.unmodifiableSet(usesClassNames);
	}

	public void addUsesClassName(String name) {
		usesClassNames.add(name);
	}

	public ModuleModel getModule() {
		return module;
	}

	public void setModule(ModuleModel module) {
		if (this.module != null) {
			this.module.classes.remove(this);
		}
		this.module = module;
		if (module != null) {
			module.addClass(this);
		}
	}

	public void resolveDependencies() {
		for (String usesClassName : usesClassNames) {
			ClassModel classModel = projectModel.getClassModel(usesClassName);
			if (classModel != null) {
				addUsesClass(classModel);
			}
		}
	}

}
