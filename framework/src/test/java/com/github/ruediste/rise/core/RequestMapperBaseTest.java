package com.github.ruediste.rise.core;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.AsmUtil.MethodRef;
import com.google.common.collect.BiMap;

@RunWith(MockitoJUnitRunner.class)
public class RequestMapperBaseTest {

	private static class RequestMapper extends RequestMapperBase {

		protected RequestMapper() {
			super(A.class);
		}

		@Override
		protected RequestParseResult createParseResult(
				ActionInvocation<String> path) {
			return null;
		}

	}

	@SuppressWarnings("unused")
	private abstract class A {
		public ActionResult a() {
			return null;
		}

		public ActionResult ab() {
			return null;
		}

		public ActionResult c(Integer i) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private class B extends A {
		public ActionResult b() {
			return null;
		};

		@Override
		public ActionResult ab() {
			return null;
		};

		public ActionResult c(Boolean i) {
			return null;
		}
	}

	@Mock
	Logger log;
	@Mock
	PathInfoIndex pathInfoIndex;

	@Mock
	ClassHierarchyIndex index;

	@Mock
	CoreConfiguration config;

	@InjectMocks
	ControllerReflectionUtil util;

	@InjectMocks
	RequestMapper mapper;

	ClassNode a = AsmUtil.readClass(A.class);
	ClassNode b = AsmUtil.readClass(B.class);

	@Before
	public void before() {
		mapper.util = util;
		when(index.tryGetNode(any())).thenReturn(Optional.empty());
		when(index.tryGetNode(a.name)).thenReturn(Optional.of(a));
		when(index.tryGetNode(b.name)).thenReturn(Optional.of(b));
		when(config.calculateControllerName(a)).thenReturn("/a/");
		when(config.calculateControllerName(b)).thenReturn("/b/");
	}

	@Test
	public void testHierarchy() {
		mapper.register(b);
		assertThat(mapper.actionMethodNameMap.keySet(), not(contains(a.name)));
		BiMap<MethodRef, String> map = mapper.actionMethodNameMap.get(b.name);
		assertNotNull(map);
		assertThat(
				map.keySet(),
				containsInAnyOrder(
						new MethodRef(a.name, "a", "()"
								+ Type.getDescriptor(ActionResult.class)),
						new MethodRef(a.name, "c", "(Ljava/lang/Integer;)"
								+ Type.getDescriptor(ActionResult.class)),
						new MethodRef(b.name, "c", "(Ljava/lang/Boolean;)"
								+ Type.getDescriptor(ActionResult.class)),
						new MethodRef(b.name, "ab", "()"
								+ Type.getDescriptor(ActionResult.class)),
						new MethodRef(b.name, "b", "()"
								+ Type.getDescriptor(ActionResult.class))));
	}
}
