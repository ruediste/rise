package com.github.ruediste.laf.mvc.web;

import static org.mockito.Mockito.when;
import net.sf.cglib.asm.Type;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;

@RunWith(MockitoJUnitRunner.class)
public class MvcWebRequestMapperImplTest {
	@Mock
	ClassHierarchyCache cache;

	@Mock
	PathInfoIndex index;

	@InjectMocks
	MvcWebRequestMapperImpl mapper;

	private class A implements IControllerMvcWeb {

	}

	@Before
	public void setup() {
		when(cache.getChildren(Type.getInternalName(IControllerMvcWeb.class)))
				.thenReturn();
		mapper.registerControllers();
	}

	@Test
	public void testSimple() {

	}
}
