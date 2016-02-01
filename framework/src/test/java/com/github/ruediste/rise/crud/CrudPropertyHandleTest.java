package com.github.ruediste.rise.crud;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.rise.core.persistence.DbTestBase;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.RisePersistenceUtil;
import com.github.ruediste.rise.core.persistence.TestEntity;
import com.github.ruediste.rise.nonReloadable.front.reload.MemberOrderIndex;

@RunWith(MockitoJUnitRunner.class)
public class CrudPropertyHandleTest extends DbTestBase {

    @Mock
    MemberOrderIndex index;

    @InjectMocks
    CrudReflectionUtil util;

    @Inject
    RisePersistenceUtil persistenceUtil;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void before() {
        util.persistenceUtil = persistenceUtil;
        when(index.orderMembers(any(Class.class), any(Collection.class)))
                .thenAnswer(invocation -> new ArrayList<>(
                        (Collection) invocation.getArguments()[1]));
    }

    @Test
    public void getAndSetPropertyValueField() {
        PersistentType type = util.getPersistentType(null, TestEntity.class);
        CrudPropertyInfo info = util.getAllPropertiesMap(type).get("value");
        TestEntity e = new TestEntity();
        CrudPropertyHandle p = CrudPropertyHandle.create(info, () -> e, () -> e,
                null);
        e.setValue("foo");
        assertEquals("foo", p.getValue());
        p.setValue("bar");
        assertEquals("bar", e.getValue());
    }
}
