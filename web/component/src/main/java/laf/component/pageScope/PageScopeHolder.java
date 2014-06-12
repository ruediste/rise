package laf.component.pageScope;

import java.util.HashMap;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

public class PageScopeHolder {

	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	HashMap<Contextual<?>, ScopedInstance<?>> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> ScopedInstance<T> getScopedInstance(Contextual<T> contextual) {
		return (ScopedInstance<T>) map.get(contextual);
	}

	public <T> T get(Contextual<T> contextual) {
		ScopedInstance<T> scopedInstance = getScopedInstance(contextual);
		if (scopedInstance == null) {
			return null;
		} else {
			return scopedInstance.instance;
		}
	}

	public <T> T getOrCreate(Contextual<T> contextual, CreationalContext<T> ctx) {
		ScopedInstance<T> scopedInstance = getScopedInstance(contextual);
		if (scopedInstance == null) {
			T instance = contextual.create(ctx);
			scopedInstance = new ScopedInstance<>();
			scopedInstance.contextual = contextual;
			scopedInstance.ctx = ctx;
			scopedInstance.instance = instance;
			map.put(contextual, scopedInstance);
		}
		return scopedInstance.instance;
	}

	private static class ScopedInstance<T> {
		Contextual<T> contextual;
		T instance;
		CreationalContext<T> ctx;

		public void destroy() {
			contextual.destroy(instance, ctx);
		}
	}

	public void destroy() {
		for (ScopedInstance<?> instance : map.values()) {
			instance.destroy();
		}
	}

}
