package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.core.aop.AopUtil;
import com.github.ruediste.rise.core.aop.AroundAdvice;
import com.github.ruediste.rise.core.aop.InterceptedInvocation;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
import com.github.ruediste.rise.core.persistence.em.PersisteUnitRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLink;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

public class PersistenceRestartableModule extends AbstractModule {
	private static AttachedProperty<AttachedPropertyBearer, EntityManagerSet> ownEntityManagerSetProperty = new AttachedProperty<>(
			"ownEntityManagerSet");

	private Injector permanentInjector;

	public PersistenceRestartableModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;
	}

	@Override
	protected void configure() throws Exception {

		// register DBLinks
		DataBaseLinkRegistry registry = permanentInjector.getInstance(DataBaseLinkRegistry.class);
		for (DataBaseLink link : registry.getLinks()) {
			Class<? extends Annotation> requiredQualifier = link.getQualifier();
			// bind data source
			bind(DataSource.class).annotatedWith(requiredQualifier)
					.toProvider(() -> link.getDataSourceManager().getDataSource()).in(Singleton.class);

			// bind EMF to registry
			{
				bind(EntityManagerFactory.class).annotatedWith(link.getQualifier())
						.toProvider(new Provider<EntityManagerFactory>() {
							@Inject
							PersisteUnitRegistry registry;

							@Override
							public EntityManagerFactory get() {
								return registry.getUnit(requiredQualifier).orElseThrow(() -> new RuntimeException(
										"no persistence unit registered for " + requiredQualifier));
							}
						});
			}

			// bind EM to proxy
			bind(EntityManager.class).annotatedWith(requiredQualifier).toProvider(new Provider<EntityManager>() {
				@Inject
				EntityManagerHolder holder;

				@Override
				public EntityManager get() {
					return (EntityManager) Enhancer.create(EntityManager.class, new Dispatcher() {

						@Override
						public Object loadObject() throws Exception {
							return holder.getEntityManager(requiredQualifier);
						}
					});

				}
			}).in(Singleton.class);
		}

		registerOwnEntityManagersAdvice();
		registerTransactionalAdvice();

	}

	private void registerTransactionalAdvice() {
		AroundAdvice advice = new AroundAdvice() {
			@Inject
			TransactionControl trx;

			@Override
			public Object intercept(InterceptedInvocation invocation) throws Throwable {
				Transactional transactional = findTransactional(invocation.getMethod());
				if (transactional == null)
					throw new RuntimeException("No @Transactional annotation found on " + invocation.getMethod()
							+ ". This code should not have been reached");
				ITransactionControl trxe = trx.executor().propagation(transactional.propagation())
						.isolation(transactional.isolation()).timeout(transactional.timeout())
						.updating(transactional.updating()).rollbackFor(transactional.rollbackFor())
						.noRollbackFor(transactional.noRollbackFor());

				if (transactional.forceNewEntityManagerSet())
					trxe = trxe.forceNewEntityManagerSet();

				return trxe.execute(() -> {
					try {
						return invocation.proceed();
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				});

			}
		};

		requestInjection(advice);

		AopUtil.registerSubclass(config().standardConfig, t -> true, (t, m) -> findTransactional(m) != null, advice);
	}

	private Transactional findTransactional(Method m) {
		Transactional result = m.getDeclaredAnnotation(Transactional.class);
		if (result != null)
			return result;
		Class<?> cls = m.getDeclaringClass();
		while (cls != null) {
			result = cls.getDeclaredAnnotation(Transactional.class);
			if (result != null)
				return result;
			cls = cls.getSuperclass();
		}
		return null;
	}

	protected void registerOwnEntityManagersAdvice() {
		AroundAdvice advice = new AroundAdvice() {
			@Inject
			EntityManagerHolder holder;

			@Override
			public Object intercept(InterceptedInvocation invocation) throws Throwable {
				EntityManagerSet set = ownEntityManagerSetProperty.setIfAbsent(invocation.getPropertyBearer(),
						() -> holder.createEntityManagerSet());
				EntityManagerSet old = holder.setCurrentEntityManagerSet(set);
				try {
					return invocation.proceed();
				} finally {
					holder.setCurrentEntityManagerSet(old);
				}
			}
		};

		requestInjection(advice);
		// register OwnEntityManagers advice
		AopUtil.registerSubclass(config().standardConfig,
				t -> t.getRawType().isAnnotationPresent(OwnEntityManagers.class),
				(t, m) -> !m.isAnnotationPresent(SkipOfOwnEntityManagers.class), advice);

	}

	@Provides
	TransactionManager transactionManager() {
		return permanentInjector.getInstance(TransactionManager.class);
	}

	@Provides
	TransactionSynchronizationRegistry transactionSynchronizationRegistry() {
		return permanentInjector.getInstance(TransactionSynchronizationRegistry.class);
	}
}
