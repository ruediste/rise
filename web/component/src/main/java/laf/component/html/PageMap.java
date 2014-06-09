package laf.component.html;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.ComponentView;

@SessionScoped
@Stateful
public class PageMap {

	@Inject
	Instance<PageManager> pageManagerInstance;

	private long nextId = 0;
	private Map<Long, PageManager> map = new HashMap<>();

	public long register(ComponentView<?> view) {
		long id = nextId++;
		PageManager manager = pageManagerInstance.get();
		manager.initialize(view);
		map.put(id, manager);
		return id;
	}

	public PageManager get(long id) {
		return map.get(id);
	}

	public void remove(long id) {
		PageManager manager = map.remove(id);
		manager.destroy();
	}

}
