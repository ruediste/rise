package laf.component.core.pageScope;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Manages the Page Scope.
 *
 * <p>
 * This class is used to enter and leave the page scope
 * </p>
 */
@ApplicationScoped
public class PageScopeManager {

	@Inject
	PageScopeHolderMap map;

	private final ThreadLocal<PageScopeHolder> currentHolder = new ThreadLocal<>();

	private static PageScopeManager instance;

	public static PageScopeManager getInstance() {
		return instance;
	}

	@PostConstruct
	public void initialize() {
		instance = this;
	}

	/**
	 * enter a new page scope
	 */
	public long enterNew() {
		if (isActive()) {
			throw new IllegalStateException("Scope already active");
		}
		PageScopeHolder holder = map.create();
		currentHolder.set(holder);

		try {
			holder.semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return holder.getId();
	}

	/**
	 * enter the given page scope
	 */
	public void enter(long id) {
		if (isActive()) {
			throw new IllegalStateException("Scope already active");
		}
		PageScopeHolder holder = map.get(id);
		try {
			holder.semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		currentHolder.set(holder);
	}

	/**
	 * Return the id of the current scope, or null if none is active
	 */
	public Long getId() {
		PageScopeHolder holder = currentHolder.get();
		return holder == null ? null : holder.getId();
	}

	public boolean isActive() {
		return currentHolder.get() != null;
	}

	/**
	 * Leave the current page scope
	 */
	public void leave() {
		if (!isActive()) {
			throw new IllegalStateException("no scope active");
		}
		currentHolder.get().semaphore.release();
		currentHolder.set(null);
	}

	PageScopeHolder getCurrentHolder() {
		return currentHolder.get();
	}

	/**
	 * Destroy the given page scope. May not be the current scope
	 */
	public void destroy(long id) {
		map.destroy(id);
	}
}
