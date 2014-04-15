package laf.initialization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

@Singleton
public class InitializationService {

	@Inject
	Event<CreateInitializersEvent> createInitializersEvent;

	private static class MethodInitializer extends InitializerImpl {

		ArrayList<InitializerMatcher> beforeMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> beforeMatchersOptional = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchersOptional = new ArrayList<>();
		private Object component;
		private Method method;
		private final HashSet<Class<?>> relatedRepresentingClasses = new HashSet<>();

		public MethodInitializer(Method method, Object component,
				LafInitializer lafInitializer) {
			super(component.getClass());
			this.method = method;
			this.component = component;

			for (Class<?> cls : lafInitializer.before()) {
				beforeMatchers.add(new InitializerMatcher(cls));
				relatedRepresentingClasses.add(cls);
			}

			for (Class<?> cls : lafInitializer.beforeOptional()) {
				beforeMatchersOptional.add(new InitializerMatcher(cls));
				relatedRepresentingClasses.add(cls);
			}
			for (Class<?> cls : lafInitializer.after()) {
				afterMatchers.add(new InitializerMatcher(cls));
				relatedRepresentingClasses.add(cls);
			}
			for (Class<?> cls : lafInitializer.afterOptional()) {
				afterMatchersOptional.add(new InitializerMatcher(cls));
				relatedRepresentingClasses.add(cls);
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

		@Override
		public Set<Class<?>> getRelatedRepresentingClasses() {
			return relatedRepresentingClasses;
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
			Set<Initializer> initializers) {
		// determine depends relation
		DependsRelation dependsRelation = calculateDependsRelation(initializers);

		// determine required initializers
		Set<Initializer> requiredInitializers = new HashSet<>();
		{
			DepthFirstIterator<Initializer, Edge> it = new DepthFirstIterator<>(
					dependsRelation.mandatory, rootInitializer);
			while (it.hasNext()) {
				requiredInitializers.add(it.next());
			}
		}

		// iterate over the initializers using the combined map (all
		// dependencies) and
		// filter out the required initializers
		ArrayList<Initializer> orderedInitializers = new ArrayList<>();
		{
			// create subgraph
			DirectedSubgraph<Initializer, Edge> subgraph;
			{
				HashSet<Initializer> set = new HashSet<>();
				DepthFirstIterator<Initializer, Edge> it = new DepthFirstIterator<>(
						dependsRelation.combined, rootInitializer);
				while (it.hasNext()) {
					set.add(it.next());
				}
				subgraph = new DirectedSubgraph<>(dependsRelation.combined,
						set, null);
			}

			// traverse graph
			{
				Queue<Initializer> queue = new PriorityQueue<>(10,
						new Comparator<Initializer>() {

							@Override
							public int compare(Initializer o1, Initializer o2) {
								return o1
										.getRepresentingClass()
										.getName()
										.compareTo(
												o2.getRepresentingClass()
														.getName());
							}
						});
				TopologicalOrderIterator<Initializer, Edge> it = new TopologicalOrderIterator<Initializer, Edge>(
						subgraph, queue);
				while (it.hasNext()) {
					Initializer next = it.next();
					if (requiredInitializers.contains(next)) {
						orderedInitializers.add(next);
					}
				}
			}
		}

		// run initializers
		for (int i = orderedInitializers.size() - 1; i >= 0; i--) {
			orderedInitializers.get(i).run();
		}
	}

	private class Edge {

	}

	private class DependsRelation {
		SimpleDirectedGraph<Initializer, Edge> mandatory;
		SimpleDirectedGraph<Initializer, Edge> combined;

		public DependsRelation() {
			EdgeFactory<Initializer, Edge> edgeFactory = new EdgeFactory<Initializer, Edge>() {

				@Override
				public Edge createEdge(Initializer sourceVertex,
						Initializer targetVertex) {
					return new Edge();
				}
			};
			mandatory = new SimpleDirectedGraph<>(edgeFactory);
			combined = new SimpleDirectedGraph<>(edgeFactory);
		}
	}

	DependsRelation calculateDependsRelation(Iterable<Initializer> initializers) {
		DependsRelation result = new DependsRelation();

		ClassMap<Object, Initializer> initializersByRepresentingClass = new ClassMap<>();

		for (Initializer i : initializers) {
			initializersByRepresentingClass.put(i.getRepresentingClass(), i);
			result.mandatory.addVertex(i);
			result.combined.addVertex(i);
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
						result.combined.addEdge(r.getSource(), r.getTarget());
						if (!r.isOptional()) {
							result.mandatory.addEdge(r.getSource(),
									r.getTarget());
						}
					}

				}
			}
		}
		return result;
	}

	public class CreateInitializersEventImpl implements CreateInitializersEvent {
		final Set<Initializer> initializers = new HashSet<>();
		final Set<Object> checkedObjects = new HashSet<>();

		@Override
		public void addInitializer(Initializer initializer) {
			initializers.add(initializer);
		}

		@Override
		public void createInitializersFrom(Object object) {
			if (object instanceof Iterable<?>) {
				for (Object o : (Iterable<?>) object) {
					if (!checkedObjects.contains(object)) {
						initializers.addAll(createInitializers(o));
						addCheckedObject(o);
					}
				}
			} else {
				if (!checkedObjects.contains(object)) {
					initializers.addAll(createInitializers(object));
					addCheckedObject(object);
				}
			}
		}

		@Override
		public void addCheckedObject(Object o) {
			checkedObjects.add(o);
		}

		@Override
		public boolean isChecked(Object o) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public void initialize(Class<?> rootInitializerRepresentingClass) {
		// create initializers
		CreateInitializersEventImpl e = new CreateInitializersEventImpl();
		createInitializersEvent.fire(e);

		// find root initializer
		Initializer root = null;
		for (Initializer i : e.initializers) {
			if (rootInitializerRepresentingClass.isAssignableFrom(i
					.getRepresentingClass())) {
				if (root == null) {
					root = i;
				} else {
					throw new Error(
							"Multiple Initializers with representing class "
									+ rootInitializerRepresentingClass
											.getName()
									+ " found. Only one expected as root initializer.");
				}
			}
		}

		// run initializers
		runInitializers(root, e.initializers);
	}

}
