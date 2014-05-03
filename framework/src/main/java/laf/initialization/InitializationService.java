package laf.initialization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;

@Singleton
public class InitializationService {

	@Inject
	Event<CreateInitializersEvent> createInitializersEvent;

	@Inject
	Logger log;

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
		public Collection<InitializerDependsRelation> getDeclaredRelations(
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

	/**
	 * Iterate over the provided objects and create the initializers for each
	 * one.
	 */
	public Collection<Initializer> createInitializers(
			Class<? extends InitializationPhase> initializationPhase, Iterable<?> objects) {
		ArrayList<Initializer> result = new ArrayList<>();
		for (Object object : objects) {
			result.addAll(createInitializers(initializationPhase, object));
		}
		return result;
	}

	/**
	 * Determine if instances of the provided class might define initializers.
	 * If this method returns false, instances of the provided class never
	 * define initializers. If it returns true, instances can define
	 * initializers, but it is possible that a concrete instance does not define
	 * any initializers.
	 */
	public boolean mightDefineInitializers(Class<?> clazz) {
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
	 * Scan the provided object for defined initializers.
	 *
	 * <p>
	 * If the method implements {@link InitializerProvider}, the implementation
	 * will be invoked to create initializers. In addition the class is scanned
	 * for methods annotated with {@link LafInitializer}. For each such method
	 * an {@link Initializer} is created, which will call the method.
	 * Dependencies are declared with the annotation.
	 */
	public Collection<Initializer> createInitializers(
			Class<? extends InitializationPhase> initializationPhase, Object object) {
		if (object == null) {
			return Collections.emptyList();
		}

		ArrayList<Initializer> result = new ArrayList<>();

		// handle InitializerProvider
		if (object instanceof InitializerProvider) {
			for (Object obj : ((InitializerProvider) object)
					.getInitializers(initializationPhase)) {
				if (obj instanceof Initializer) {
					result.add((Initializer) obj);
				} else {
					result.addAll(createInitializers(initializationPhase, obj));
				}
			}
		}

		Class<? extends Object> representingClass = object.getClass();
		for (Method method : representingClass.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer == null) {
				continue;
			}
			if (!initializationPhase.equals(lafInitializer.phase())) {
				continue;
			}
			result.add(new MethodInitializer(method, object, lafInitializer));
		}
		return result;
	}

	/**
	 * Run some of the provided initializers
	 *
	 * @param rootInitializer
	 *            all initializers this initializer depends on will be executed.
	 * @param initializers
	 *            the set of initializers to run
	 */
	public void runInitializers(Initializer rootInitializer,
			Set<Initializer> initializers) {
		// determine depends relation
		DependsRelation dependsRelation = calculateDependsRelation(initializers);

		// check for loops
		{
			CycleDetector<Initializer, Edge> detector = new CycleDetector<>(
					dependsRelation.combined);
			Set<Initializer> findCycles = detector.findCycles();
			if (!findCycles.isEmpty()) {
				throw new Error(
						"Found cycle in initializer dependency graph. Participating initializers: "
								+ findCycles);
			}
		}

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
				HashSet<Initializer> reachableInitializers = new HashSet<>();
				DepthFirstIterator<Initializer, Edge> it = new DepthFirstIterator<>(
						dependsRelation.combined, rootInitializer);
				while (it.hasNext()) {
					reachableInitializers.add(it.next());
				}
				subgraph = new DirectedSubgraph<>(dependsRelation.combined,
						reachableInitializers, null);
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
			Initializer initializer = orderedInitializers.get(i);
			log.debug("Running initializer " + initializer);
			initializer.run();
		}
	}

	private static class Edge {

	}

	private static class DependsRelation {
		/**
		 * Contains mandatory depends relations only
		 */
		SimpleDirectedGraph<Initializer, Edge> mandatory;

		/**
		 * Contains all depends relations, mandatory and optional
		 */
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

	private class CreateInitializersEventImpl implements
			CreateInitializersEvent {
		/**
		 * Contains the initializers created via
		 * {@link #createInitializersFrom(Object)}
		 */
		HashMap<Object, Collection<Initializer>> objectBasedInitializers = new HashMap<>();

		/**
		 * Contains all initializers
		 */
		final Set<Initializer> initializers = new HashSet<>();

		private final Class<? extends InitializationPhase> initializationPhase;

		public CreateInitializersEventImpl(Class<? extends InitializationPhase> initializationPhase) {
			this.initializationPhase = initializationPhase;

		}

		@Override
		public void addInitializer(Initializer initializer) {
			initializers.add(initializer);
		}

		private Collection<Initializer> createInitializersFromInner(
				Class<? extends InitializationPhase> initializationPhase, Object object) {
			Collection<Initializer> result = objectBasedInitializers
					.get(object);
			if (result == null) {
				result = createInitializers(initializationPhase, object);
				objectBasedInitializers.put(object, result);
				initializers.addAll(result);
			}
			return result;
		}

		@Override
		public Collection<Initializer> createInitializersFrom(Object object) {
			if (object instanceof Iterable<?>) {
				Collection<Initializer> initializers = objectBasedInitializers
						.get(object);
				if (initializers == null) {
					initializers = new ArrayList<>();
					for (Object o : (Iterable<?>) object) {
						initializers.addAll(createInitializersFromInner(initializationPhase,
								o));
					}
					objectBasedInitializers.put(object, initializers);
				}
				return initializers;
			} else {
				return createInitializersFromInner(initializationPhase, object);
			}
		}

		@Override
		public Class<? extends InitializationPhase> getPhase() {
			return initializationPhase;
		}
	}

	/**
	 * Discover initializers using the {@link CreateInitializersEvent}, search
	 * for the single instance of the rootInitializerRepresentingClass and run
	 * the initializers.
	 */
	public void initialize(Class<? extends InitializationPhase> initializationPhase,
			Class<?> rootInitializerRepresentingClass) {
		// create initializers
		CreateInitializersEventImpl e = new CreateInitializersEventImpl(initializationPhase);
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

		if (root == null) {
			throw new Error("No Root initializer instance found");
		}

		// run initializers
		runInitializers(root, e.initializers);
	}

}
