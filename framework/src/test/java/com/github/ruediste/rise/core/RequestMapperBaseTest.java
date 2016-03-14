package com.github.ruediste.rise.core;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
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
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationParameter;
import com.github.ruediste.rise.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.nonReloadable.SignatureHelper;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.AsmUtil.MethodRef;
import com.github.ruediste.rise.util.AsmUtil.OverrideDesc;
import com.github.ruediste.rise.util.MethodInvocation;
import com.google.common.collect.BiMap;

@RunWith(MockitoJUnitRunner.class)
public class RequestMapperBaseTest {

    private static class RequestMapper extends RequestMapperBase {

        protected RequestMapper() {
            super(A.class);
        }

        @Override
        protected RequestParseResult createParseResult(ActionInvocation<String> path) {
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
    MethodRef a_a = new MethodRef(a.name, "a", "()" + Type.getDescriptor(ActionResult.class));
    MethodRef a_c = new MethodRef(a.name, "c", "(Ljava/lang/Integer;)" + Type.getDescriptor(ActionResult.class));

    String sessionId = "12345";

    @Before
    public void before() {

        mapper.util = util;
        mapper.signatureHelper = new SignatureHelper();
        mapper.signatureHelper.postConstruct();

        when(index.tryGetNode(any())).thenReturn(Optional.empty());
        when(index.tryGetNode(a.name)).thenReturn(Optional.of(a));
        when(index.tryGetNode(b.name)).thenReturn(Optional.of(b));
        when(config.calculateControllerName(a)).thenReturn("a");
        when(config.calculateControllerName(b)).thenReturn("b");
        config.doUrlSigning = true;
        config.urlSignatureBytes = 20;
    }

    @Test
    public void testHierarchy() {
        mapper.register(b);
        assertThat(mapper.actionMethodNameMap.keySet(), not(contains(a.name)));
        BiMap<OverrideDesc, String> map = mapper.actionMethodNameMap.get(b.name);
        assertNotNull(map);
        assertThat(map.keySet(), containsInAnyOrder(new OverrideDesc("a()"), new OverrideDesc("c(Ljava/lang/Integer;)"),
                new OverrideDesc("c(Ljava/lang/Boolean;)"), new OverrideDesc("ab()"), new OverrideDesc("b()")));
    }

    @Test
    public void testGenerateBaseClass() throws Throwable {
        mapper.register(b);
        ActionInvocation<String> invocation = createInvocationToA_a();
        PathInfo pathInfo = mapper.generate(invocation, () -> sessionId).getPathInfo();
        assertEquals("/b.a", pathInfo.getValue());
    }

    ActionInvocationParameter testParameter = new ActionInvocationParameter("TEST");

    @Test
    public void parameterRoundtrip() throws Exception {
        mapper.register(a);
        ActionInvocation<String> invocation = createInvocationToA_a();
        testParameter.set(invocation, "foo");
        UrlSpec spec = mapper.generate(invocation, () -> sessionId);
        ActionInvocation<String> parsed = mapper.parse("/a.a", a, a_a, new HttpRequestImpl(spec), () -> sessionId);
        assertEquals("foo", testParameter.get(parsed));
    }

    @Test
    public void signatureChecked() throws Exception {
        mapper.register(a);
        ActionInvocation<String> invocation = createInvocationToA_a();
        UrlSpec spec = mapper.generate(invocation, () -> sessionId);
        try {
            mapper.parse("/a.a", a, a_a, new HttpRequestImpl(spec), () -> "1234567");
            fail();
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("URL signature"))
                throw e;
        }
    }

    @Test
    public void roundtripWithArg() throws Exception {
        mapper.register(a);
        ActionInvocation<String> invocation = new ActionInvocation<>();
        invocation.methodInvocation = new MethodInvocation<>(A.class, A.class.getMethod("c", Integer.class));
        invocation.methodInvocation.getArguments().add("1");

        UrlSpec spec = mapper.generate(invocation, () -> sessionId);
        ActionInvocation<String> parsed = mapper.parse("/a.c", a, a_c, new HttpRequestImpl(spec), () -> sessionId);
        assertNotNull(parsed);
    }

    private ActionInvocation<String> createInvocationToA_a() throws NoSuchMethodException {
        ActionInvocation<String> invocation = new ActionInvocation<>();
        invocation.methodInvocation = new MethodInvocation<>(A.class, A.class.getMethod("a"));
        return invocation;
    }
}
