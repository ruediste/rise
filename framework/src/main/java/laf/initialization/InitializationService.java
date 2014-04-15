package laf.initialization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.inject.Singleton;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

@Singleton
public class InitializationService {

	private static class MethodInitializer extends InitializerImpl {

		ArrayList<InitializerMatcher> beforeMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> beforeMatchersOptional = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchersOptional = new ArrayList<>();
		private Object component;
		private Method method;

		public MethodInitializer(Method method, Object component,
				LafInitializer lafInitializer) {
			super(component.getClass());
			this.method = method;
			this.component = component;

			for (Class<?> cls : lafInitializer.before()) {
				beforeMatchers.add(new InitializerMatcher(cls));
			}

			for (Class<?> cls : lafInitializer.beforeOptional()) {
				beforeMatchersOptional.add(new InitializerMatcher(cls));
			}
			for (Class<?> cls : lafInitializer.after()) {
				afterMatchers.add(new InitializerMatcher(cls));
			}
			for (Class<?> cls : lafInitializer.afterOptional()) {
				afterMatchersOptional.add(new InitializerMatcher(cls));
			}
		}

		@Override
		public Iterable<InitializerDependsRelation> getDeclaredRelations(
				Initializer other) {
			ArrayList<InitializerDependsRelation> result = new ArrayList<>();

			for (InitializerMatcher m : beforeMatchers) {
				if (m.matches(other)) {
					result.add(new InitializerDependsRelation(other, this,
							false));
				}
			}
			for (InitializerMatcher m : beforeMatchersOptional) {
				if (m.matches(other)) {
					result.add(new InitializerDependsRelation(other, this, true));
				}
			}
			for (InitializerMatcher m : afterMatchers) {
				if (m.matches(other)) {
					result.add(new InitializerDependsRelation(this, other,
							false));
				}
			}
			for (InitializerMatcher m : afterMatchersOptional) {
				if (m.matches(other)) {
					result.add(new InitializerDependsRelation(this, other, true));
				}
			}
			return result;
		}

		@Override
		public void run() {
			try {
				method.invoke(component);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(
						"Unable to invoke initializer method " + method);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Error in initializer method "
						+ method, e.getCause());
			}
		}

		@Override
		public String toString() {
			return method.toString();
		}

	}

	public Collection<Initializer> createInitializers(Iterable<?> objects) {
		ArrayList<Initializer> result = new ArrayList<>();
		for (Object object : objects) {
			result.addAll(createInitializers(object));
		}
		return result;
	}

	public boolean mightCreateInitializers(Class<?> clazz) {
		if (InitializerProvider.class.isAssignableFrom(clazz)) {
			return true;
		}
		for (Method method : clazz.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Scan the provided object for a single method annotated with
	 * {@link LafInitializer} and create an {@link Initializer}, which will call
	 * this method. The dependencies are declared with the annotation. If no
	 * initializer method is found, null is returned.
	 */
	public Collection<Initializer> createInitializers(Object object) {
		ArrayList<Initializer> result = new ArrayList<>();

		// handle InitializerProvider
		if (object instanceof InitializerProvider) {
			for (Initializer i : ((InitializerProvider) object)
					.getInitializers()) {
				result.add(i);
			}
		}

		Class<? extends Object> representingClass = object.getClass();
		for (Method method : representingClass.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer == null) {
				continue;
			}
			result.add(new MethodInitializer(method, object, lafInitializer));
		}
		return result;
	}

	public void runInitializers(Initializer rootInitializer,
			Iterable<Initializer> initializers) {
		// check for duplicates and the uniqueness of ids
		HashSet<Initializer> initializerSet = new HashSet<>();
		for (Initializer init : initializers) {
			if (!initializerSet.add(init)) {
				throw new RuntimeException("duplication initializer detected");
			}
		}

		// determine depends relation
		DependsRelation dependsRelation =calculateDependsRelation(initializerSet);
		dependsRelation.mandatory.

		// initialize map containing after how many initializers a given
		// initializer can be run, as well as remaining
		Map<Initializer, Integer> afterCount = new LinkedHashMap<>();
		HashSet<Initializer> remaining = new LinkedHashSet<>();
		for (Initializer i : initializers) {
			afterCount.put(i, 0);
			remaining.add(i);
		}

		for (Set<Initializer> set : dedependsRelation.values()) {
			for (Initializer i : set) {
				afterCount.put(i, afterCount.get(i) + 1);
			}
		}

		// move initializers with afterCount = 0 from remaining to runnable
		LinkedHashSet<Initializer> runnable = new LinkedHashSet<>();
		for (Initializer i : initializers) {
			if (afterCount.get(i).equals(0)) {
				remaining.remove(i);
				runnable.add(i);
			}
		}

		// run initializers
		while (!runnable.isEmpty()) {
			Initializer i = runnable.iterator().next();
			runnable.remove(i);
			for (Initializer p : depedependsRelation)) {
				int newCount = afterCount.get(p) - 1;
				afterCount.put(p, newCount);
				if (newCount == 0) {
					remaining.remove(p);
					runnable.add(p);
				}
			}

			// run initializer
			i.run();
		}

		if (!remaining.isEmpty()) {
			throw new RuntimeException(
					"There was a loop in the initializer dependencies. Remaining Initializers: "
							+ initializers + " Dependencies:" + dependdependsRelation);
		}
	}

	private class Edge {

	}

	private class DependsRelation {
		SimpleDirectedGraph<Initializer, Edge> mandatory;
		SimpleDirectedGraph<Initializer, Edge> optional;

		public DependsRelation() {
			EdgeFactory<Initializer, Edge> edgeFactory = new EdgeFactory<Initializer, Edge>() {

				@Override
				public Edge createEdge(Initializer sourceVertex,
						Initializer targetVertex) {
					return new Edge();
				}
			};
			mandatory = new SimpleDirectedGraph<>(edgeFactory);
			optional = new SimpleDirectedGraph<>(edgeFactory);
		}
	}

	DependsRelation calculateDependsRelation(Iterable<Initializer> initializers) {
		DependsRelation result = new DependsRelation();

		ClassMap<Object, Initializer> initializersByRepresentingClass = new ClassMap<>();

		for (Initializer i : initializers) {
			initializersByRepresentingClass.put(i.getRepresentingClass(), i);
		}

		for (Initializer i : initializers) {
			Set<Class<?>> relatedRepresentingClasses = i
					.getRelatedRepresentingClasses();
			if (relatedRepresentingClasses == null) {
				relatedRepresentingClasses = Collections
						.<Class<?>> singleton(Object.class);
			}
			for (Class<?> cls : relatedRepresentingClasses) {
				for (Initializer p : initializersByRepresentingClass.get(cls)) {
					for (InitializerDependsRelation r : i
							.getDeclaredRelations(p)) {
						if (r.isOptional()) {
							result.optional.addEdge(r.getSource(),
									r.getTarget());
						} else {
							result.mandatory.addEdge(r.getSource(),
									r.getTarget());
						}
					}

				}
			}
		}
		return result;
	}

	void initialize(Class<?> rootInitializerRepresentingClass) {

	}

}
