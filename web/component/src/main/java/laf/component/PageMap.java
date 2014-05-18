package laf.component;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

@Stateful
public class PageMap {

	private long nextId = 0;
	private Map<Long, ComponentView<?>> map = new HashMap<>();

	public long register(ComponentView<?> view) {
		long id = nextId++;
		map.put(id, view);
		return id;
	}

	public ComponentView<?> get(long id) {
		return map.get(id);
	}

	public void remove(long id) {
		map.remove(id);
	}

}