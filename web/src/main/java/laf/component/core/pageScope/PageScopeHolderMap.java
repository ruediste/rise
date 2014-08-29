package laf.component.core.pageScope;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.PostRemove;

@SessionScoped
public class PageScopeHolderMap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	Instance<PageScopeHolder> holderInstance;

	private final AtomicLong nextId = new AtomicLong();
	private ConcurrentMap<Long, PageScopeHolder> map = new ConcurrentHashMap<>();

	public PageScopeHolder create() {
		long id = nextId.incrementAndGet();
		PageScopeHolder holder = holderInstance.get();
		holder.setId(id);
		map.put(id, holder);
		return holder;
	}

	public PageScopeHolder get(long id) {
		return map.get(id);
	}

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

	@PostRemove
	public void remove() {
		for (PageScopeHolder holder : map.values()) {
			holder.destroy();
		}
	}
}
