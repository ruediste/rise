package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.IControllerComponent;
import com.github.ruediste.rise.component.components.CDataGrid.Cell;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.strategy.Strategies;
import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Preconditions;

@Singleton
public class CrudUtil {

	@Inject
	Injector injector;

	@Inject
	Strategies strategies;

	public <TKey, T extends Strategy> T getStrategy(Class<T> strategy, Class<?> entityClass) {
		return strategies.getStrategies(strategy, entityClass).findFirst().orElseThrow(() -> new RuntimeException(
				"No strategy " + strategy.getName() + " found for entity class " + entityClass.getName()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> TypedQuery<T> queryWithFilters(PersistentType type, EntityManager em,
			Consumer<PersistenceFilterContext> action) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery q = cb.createQuery(type.getEntityClass());
		Root root = q.from(type.getEntityClass());
		q.select(root);

		ArrayList<Predicate> whereClauses = new ArrayList<>();
		PersistenceFilterContext filterContext = new PersistenceFilterContext<T>() {

			@Override
			public CriteriaQuery<T> query() {
				return q;
			}

			@Override
			public CriteriaBuilder cb() {
				return cb;
			}

			@Override
			public Root<T> root() {
				return root;
			}

			@Override
			public void addWhere(Predicate predicate) {
				whereClauses.add(predicate);
			}
		};

		action.accept(filterContext);

		q.where(whereClauses.toArray(new Predicate[] {}));
		return em.createQuery(filterContext.query());
	}

	interface PersistenceFilterContext<T> {
		CriteriaBuilder cb();

		CriteriaQuery<T> query();

		Root<T> root();

		void addWhere(Predicate predicate);
	}

	/**
	 * A browser displays all instances of a certain type and allows the user to
	 * search/filter the list. For each instance, certain operations can be
	 * performed.
	 */
	@ImplementedBy(DefaultBrowserFactory.class)
	public interface BrowserFactory extends Strategy {
		Object createBrowser(Class<?> entityClass, Class<? extends Annotation> emQualifier);
	}

	private static class DefaultBrowserFactory implements BrowserFactory {

		@Inject
		Provider<DefaultCrudBrowserController> provider;

		@Override
		public Object createBrowser(Class<?> entityClass, Class<? extends Annotation> emQualifier) {
			return provider.get().initialize(entityClass, emQualifier);
		}

	}

	@ImplementedBy(DefaultDisplayFactory.class)
	public interface DisplayFactory extends Strategy {

		Object createDisplay(Object entity);
	}

	private static class DefaultDisplayFactory implements DisplayFactory {

		@Inject
		Provider<DefaultCrudDisplayController> provider;

		@Override
		public Object createDisplay(Object entity) {
			return provider.get().initialize(entity);
		}

	}

	@ImplementedBy(DefaultEditFactory.class)
	public interface EditFactory extends Strategy {

		Object createEdit(Object entity);
	}

	private static class DefaultEditFactory implements EditFactory {

		@Inject
		Provider<DefaultCrudEditController> provider;

		@Override
		public Object createEdit(Object entity) {
			return provider.get().initialize(entity);
		}

	}

	@ImplementedBy(DefaultCreateFactory.class)
	public interface CreateFactory extends Strategy {

		Object createCreate(Class<?> entityClass, Class<? extends Annotation> emQualifier);
	}

	private static class DefaultCreateFactory implements CreateFactory {

		@Inject
		Provider<DefaultCrudCreateController> provider;

		@Override
		public Object createCreate(Class<?> entityClass, Class<? extends Annotation> emQualifier) {
			return provider.get().initialize(entityClass, emQualifier);
		}

	}

	@ImplementedBy(DefaultDeleteFactory.class)
	public interface DeleteFactory extends Strategy {

		Object createDelete(Object entity);
	}

	private static class DefaultDeleteFactory implements DeleteFactory {

		@Inject
		Provider<DefaultCrudDeleteController> provider;

		@Override
		public Object createDelete(Object entity) {
			return provider.get().initialize(entity);
		}

	}

	@ImplementedBy(DefaultIdentificationRenderer.class)
	public interface IdentificationRenderer extends Strategy {
		void renderIdenification(BootstrapRiseCanvas<?> html, Object entity);
	}

	private static class DefaultIdentificationRenderer implements IdentificationRenderer {

		@Inject
		CrudReflectionUtil util;

		@Override
		public void renderIdenification(BootstrapRiseCanvas<?> html, Object entity) {
			if (entity == null)
				html.write("<null>");
			else {
				PersistentType type = util.getPersistentType(entity);
				html.write(util.getIdentificationProperties(type).stream()
						.map(p -> p.getProperty().getName() + ":" + String.valueOf(p.getProperty().getValue(entity)))
						.collect(joining(" ")));
			}
		}
	}

	/**
	 * A sub controller which allows to pick an instance of an entity
	 */
	public interface CrudPicker {

		/**
		 * fired when the picker is closed. The argument is the picked entity,
		 * or null if picking has been canceled
		 */
		GenericEvent<Object> pickerClosed();
	}

	@ImplementedBy(DefaultCrudPickerFactory.class)
	public interface CrudPickerFactory extends Strategy {
		CrudPicker createPicker(Class<? extends Annotation> emQualifier, Class<?> entityClass);
	}

	public static class DefaultCrudPickerFactory implements CrudPickerFactory {

		@Inject
		Provider<DefaultCrudPickerController> provider;

		@Override
		public CrudPicker createPicker(Class<? extends Annotation> emQualifier, Class<?> entityClass) {
			Preconditions.checkNotNull(entityClass, "entityClass is null");
			return provider.get().initialize(entityClass, emQualifier);
		}

	}

	/**
	 * {@link ControllerComponent} displaying a list of entities
	 */
	public interface CrudList extends IControllerComponent {

		PersistentType getType();

		DefaultCrudListController setItemActionsFactory(Function<Object, Cell> itemActionsFactory);

		DefaultCrudListController setBottomActions(Component bottomActions);

		void refresh();

	}

	@ImplementedBy(DefaultCrudListFactory.class)
	public interface CrudListFactory extends Strategy {
		/**
		 * Create a {@link CrudList} controller for the given entities.
		 * 
		 * @param emQualifier
		 *            persistence unit to use
		 * @param entityClass
		 *            entity class to display
		 * @param constantFilter
		 *            filter to use regardless of the filter entered by the user
		 */
		CrudList createList(Class<? extends Annotation> emQualifier, Class<?> entityClass,
				Consumer<PersistenceFilterContext<?>> constantFilter);
	}

	public static class DefaultCrudListFactory implements CrudListFactory {

		@Inject
		Provider<DefaultCrudListController> provider;

		@Override
		public CrudList createList(Class<? extends Annotation> emQualifier, Class<?> entityClass,
				Consumer<PersistenceFilterContext<?>> constantFilter) {
			Preconditions.checkNotNull(entityClass, "entityClass is null");
			return provider.get().initialize(entityClass, emQualifier, constantFilter);
		}

	}

}
