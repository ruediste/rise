package laf.component.core.pageScope;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.PostRemove;

/**
 * Session scoped map between page IDs and the associated
 * {@link PageScopeHolder}s.
 */
@SessionScoped
public class PageScopeHolderMap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	Instance<PageScopeHolder> holderInstance;

	private final AtomicLong nextId = new AtomicLong();
	private ConcurrentMap<Long, PageScopeHolder> map = new ConcurrentHashMap<>();

	/**
	 * Create a new {@link PageScopeHolder}, used to hold the page scope of a
	 * new page
	 */
	public PageScopeHolder create() {
		long id = nextId.incrementAndGet();
		PageScopeHolder holder = holderInstance.get();
		holder.setId(id);
		map.put(id, holder);
		return holder;
	}

	/**
	 * Return a {@link PageScopeHolder} by the page id
	 */
	public PageScopeHolder get(long id) {
		return map.get(id);
	}

	/**
	 * Destroy a {@link PageScopeHolder}
	 */
	public void destroy(long id) {
		PageScopeHolder holder = get(id);
		try {
			holder.semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		map.remove(id);
		holder.destroy();
		holder.semaphore.release();
	}

	/**
	 * Called when the session is destroyed. Destroy all page scopes.
	 */
	@PostRemove
	public void remove() {
		for (PageScopeHolder holder : map.values()) {
			holder.destroy();
		}
	}
}
