package laf.initialization;

import java.util.*;

public abstract class MatcherBasedInitializer extends InitializerImpl {

	public static class InitializerMatcherSet {
		public HashSet<InitializerMatcher> beforeMatchers = new HashSet<>();
		public HashSet<InitializerMatcher> beforeMatchersOptional = new HashSet<>();
		public HashSet<InitializerMatcher> afterMatchers = new HashSet<>();
		public HashSet<InitializerMatcher> afterMatchersOptional = new HashSet<>();

	}

	protected final InitializerMatcherSet data;
	protected HashSet<Class<?>> relatedRepresentingClasses = new HashSet<>();

	public MatcherBasedInitializer(Class<?> componentClass) {
		super(componentClass);
		data = new InitializerMatcherSet();
	}

	public MatcherBasedInitializer(Class<?> componentClass,
			InitializerMatcherSet data) {
		super(componentClass);
		this.data = data;
	}

	@Override
	public Collection<InitializerDependsRelation> getDeclaredRelations(
			Initializer other) {
		ArrayList<InitializerDependsRelation> result = new ArrayList<>();

		for (InitializerMatcher m : data.beforeMatchers) {
			if (m.matches(other)) {
				result.add(new InitializerDependsRelation(other, this, false));
			}
		}
		for (InitializerMatcher m : data.beforeMatchersOptional) {
			if (m.matches(other)) {
				result.add(new InitializerDependsRelation(other, this, true));
			}
		}
		for (InitializerMatcher m : data.afterMatchers) {
			if (m.matches(other)) {
				result.add(new InitializerDependsRelation(this, other, false));
			}
		}
		for (InitializerMatcher m : data.afterMatchersOptional) {
			if (m.matches(other)) {
				result.add(new InitializerDependsRelation(this, other, true));
			}
		}
		return result;
	}

	@Override
	public Set<Class<?>> getRelatedRepresentingClasses() {
		return relatedRepresentingClasses;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MatcherBasedInitializer other = (MatcherBasedInitializer) obj;
		return data.beforeMatchers.equals(other.data.beforeMatchers)
				&& data.beforeMatchersOptional
				.equals(other.data.beforeMatchersOptional)
				&& data.afterMatchers.equals(other.data.afterMatchers)
				&& data.afterMatchersOptional
				.equals(other.data.afterMatchersOptional);
	}
}