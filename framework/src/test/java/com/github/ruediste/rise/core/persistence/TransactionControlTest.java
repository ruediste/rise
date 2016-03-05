package com.github.ruediste.rise.core.persistence;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;
import com.github.ruediste.rise.nonReloadable.persistence.TransactionProperties;

public class TransactionControlTest extends DbTestBase {

	@Inject
	TransactionControl txc;

	@Inject
	TransactionProperties props;

	@Inject
	EntityManager em;

	boolean executed;

	@Before
	public void before() {
		executed = false;
	}

	@Test
	public void testRequiredWithDifferentIsolationHigher() {
		txc.isolation(IsolationLevel.REPEATABLE_READ).execute(() -> {
			txc.isolation(IsolationLevel.SERIALIZABLE).execute(() -> {
				assertEquals(IsolationLevel.SERIALIZABLE, props.getIsolationLevel());
			});
		});
	}

	@Test
	public void testRequiredWithDifferentIsolationLower() {
		txc.isolation(IsolationLevel.SERIALIZABLE).execute(() -> {
			txc.isolation(IsolationLevel.REPEATABLE_READ).execute(() -> {
				assertEquals(IsolationLevel.SERIALIZABLE, props.getIsolationLevel());
			});
		});
	}

	@Test
	public void testRequiredUpdatingNestedInNonUpdating() {
		TestEntity e = new TestEntity();
		txc.updating().execute(() -> {
			e.setValue("foo");
			em.persist(e);
		});
		txc.isolation(IsolationLevel.SERIALIZABLE).execute(() -> {
			txc.updating().execute(() -> {
				em.find(TestEntity.class, e.getId()).setValue("bar");
			});
		});
		txc.execute(() -> {
			assertEquals("bar", em.find(TestEntity.class, e.getId()).getValue());
		});

	}
}
