package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

public class CrudFactory {

	public interface Filter<T> {
		void applyFilter(Root<T> root, CriteriaBuilder cb);
	}

	public static class BrowserSettings<T> {
		List<Consumer<T>> operations = new ArrayList<>();
		List<Filter<T>> fixedFilters = new ArrayList<>();
		CrudModel model;
	}

	/**
	 * A browser controller displays all instances of a certain type and allows
	 * the user to search/filter the list. For each instance, certain operations
	 * can be performed.
	 */
	public <T> void createBrowserController(BrowserSettings<T> settings) {

	}
}
