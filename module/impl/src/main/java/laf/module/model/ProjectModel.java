package laf.module.model;

import java.util.*;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.SimpleDirectedGraph;

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

	public Set<ModuleModel> getMatchingModules(ClassModel clazz) {
		Set<ModuleModel> result = new HashSet<>();
		for (ModuleModel module : modules.values()) {
			if (module.isIncluded(clazz)) {
				result.add(module);
			}
		}
		return result;
	}

	void addClass(ClassModel clazz) {
		classes.put(clazz.getQualifiedName(), clazz);
	}

	void addModule(ModuleModel module) {
		modules.put(module.getQualifiedNameOfRepresentingClass(), module);
	}

	/**
	 * Resolve class names recorded during parsing to the actual model objects
	 * and calculate transitive closures.
	 */
	public void resolveDependencies() {
		for (ClassModel classModel : classes.values()) {
			classModel.resolveDependencies();
		}

		for (ModuleModel moduleModel : modules.values()) {
			moduleModel.resolveDependencies();
		}

		calculateExportedModules();
		calculateAccessibleModules();
	}

	private static class Edge {

	}

	private void calculateExportedModules() {
		SimpleDirectedGraph<ModuleModel, Edge> g = new SimpleDirectedGraph<>(
				Edge.class);

		for (ModuleModel module : modules.values()) {
			g.addVertex(module);
			for (ModuleModel exported : module.exportedModules) {
				g.addEdge(module, exported);
			}
		}

		CycleDetector<ModuleModel, Edge> detector = new CycleDetector<>(g);
		Set<ModuleModel> cycleModules = detector.findCycles();

		if (!cycleModules.isEmpty()) {
			throw new RuntimeException(
					"Found cycle in module export graph. Involved modules: "
							+ cycleModules);
		}

		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(g);

		for (ModuleModel module : modules.values()) {
			module.allExportedModules.add(module);
			for (Edge e : g.outgoingEdgesOf(module)) {
				module.allExportedModules.add(g.getEdgeTarget(e));
			}
		}
	}

	private void calculateAccessibleModules() {
		for (ModuleModel module : modules.values()) {
			module.allAccessibleModules.add(module);
			for (ModuleModel imported : module.importedModules) {
				if (imported.allExportedModules.contains(module)) {
					throw new RuntimeException(
							"Module "
									+ module
									+ ": Exported modules of module "
									+ imported
									+ " contains this module itself. Exported modules: "
									+ imported.allExportedModules);
				}
				module.allAccessibleModules.addAll(imported.allExportedModules);
			}
		}
	}

	public ModuleModel getModule(String qualifiedNameOfRepresentingClass) {
		return modules.get(qualifiedNameOfRepresentingClass);
	}

	/**
	 * Check the classes for dependencies and collect all errors
	 */
	public List<String> checkClasses() {
		ArrayList<String> errors = new ArrayList<>();
		for (ClassModel clazz : classes.values()) {
			clazz.checkDependencies(errors);
		}
		return errors;
	}

	public String details() {
		StringBuilder sb = new StringBuilder();
		for (ModuleModel module : modules.values()) {
			sb.append(module.details());
			sb.append("\n");
		}
		for (ClassModel clazz : classes.values()) {
			sb.append(clazz.details());
			sb.append("\n");
		}
		return sb.toString();
	}
}
