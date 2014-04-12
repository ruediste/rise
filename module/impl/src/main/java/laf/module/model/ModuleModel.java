package laf.module.model;

import java.util.*;

public class ModuleModel {
	private final ProjectModel projectModel;
	private final String qualifiedNameOfRepresentingClass;
	final Set<ClassModel> classes = new HashSet<ClassModel>();

	public ModuleModel(ProjectModel projectModel,
			String qualifiedNameOfRepresentingClass) {
		this.projectModel = projectModel;
		this.qualifiedNameOfRepresentingClass = qualifiedNameOfRepresentingClass;
		projectModel.addModule(this);
	}

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public String getQualifiedNameOfRepresentingClass() {
		return qualifiedNameOfRepresentingClass;
	}

	public Set<ClassModel> getClasses() {
		return Collections.unmodifiableSet(classes);
	}

	public void addClass(ClassModel clazz) {
		if (clazz.module != null) {
			clazz.module.classes.remove(clazz);
		}
		classes.add(clazz);
		clazz.module = this;
	}

	public void resolveDependencies() {

	}
}
