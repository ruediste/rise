package com.github.ruediste.rise.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.core.web.ActionPathAnnotationUtil;
import com.github.ruediste.rise.core.web.ActionPathAnnotationUtil.MethodPathInfos;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.nonReloadable.UrlSignatureHelper;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.AsmUtil.MethodRef;
import com.github.ruediste.rise.util.MethodInvocation;
import com.github.ruediste.rise.util.Pair;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

/**
 * Registers the {@link ControllerMvc}s with the {@link PathInfoIndex} during
 * {@link #initialize()} and supports URL generation by providing
 * {@link #generate(ActionInvocation, String)}
 */
public abstract class RequestMapperBase implements RequestMapper {

    private static final String SIGNATURE_PARAMETER_NAME = "SIGN";

    @Inject
    Logger log;

    @Inject
    CoreConfiguration coreConfig;

    @Inject
    ClassHierarchyIndex cache;

    @Inject
    PathInfoIndex idx;

    @Inject
    ControllerReflectionUtil util;

    @Inject
    UrlSignatureHelper urlSignatureHelper;

    @Inject
    CoreRequestInfo coreRequestInfo;

    /**
     * Map controller methods to their prefixes. Prefixes do not include a final
     * "." or "/". Used for {@link #generate(ActionInvocation, String)}.
     */
    final HashMap<Pair<String, MethodRef>, String> methodToPrefixMap = new HashMap<>();

    /**
     * Map controller methods to prefixes. Unlike {@link #methodToPrefixMap},
     * the instance class of the controller is not included. The entries are
     * only used if there is exactly one prefix per {@link MethodRef}. The map
     * allows {@link #generate(ActionInvocation, String)} to generate paths if
     * the base class is given instead of the actual registered controller
     * class.
     */
    final Multimap<MethodRef, String> baseMethodToPrefixMap = MultimapBuilder
            .hashKeys().arrayListValues().build();

    /**
     * Map between methods and their action method names, grouped by class
     */
    final HashMap<String, BiMap<MethodRef, String>> actionMethodNameMap = new HashMap<>();

    private Class<?> controllerInterface;

    protected RequestMapperBase(Class<?> controllerBaseClass) {
        this.controllerInterface = controllerBaseClass;

    }

    @Override
    public void initialize() {
        String internalName = Type.getInternalName(controllerInterface);
        for (ClassNode cls : cache.getAllChildren(internalName)) {
            // skip abstract methods
            if ((cls.access & Opcodes.ACC_ABSTRACT) != 0)
                continue;
            register(cls);
        }
    }

    void register(ClassNode cls) {
        String controllerName = coreConfig.calculateControllerName(cls);
        log.debug("found controller " + cls.name + " -> " + controllerName);

        HashSet<String> subclassMethods = new HashSet<>();

        // build method name map
        BiMap<MethodRef, String> methodNameMap = HashBiMap.create();
        actionMethodNameMap.put(cls.name, methodNameMap);

        ClassNode instanceCls = cls;
        // TODO: testing
        while (cls != null) {
            HashSet<String> addedMethods = new HashSet<>();

            if (cls.methods != null)
                for (MethodNode m : cls.methods) {
                    if (!util.isActionMethod(m)) {
                        continue;
                    }
                    String name = m.name;

                    String overrideDesc = AsmUtil.getOverrideDesc(m.name,
                            m.desc);
                    if (subclassMethods.contains(overrideDesc))
                        continue;
                    addedMethods.add(overrideDesc);
                    log.debug("found action method " + name);

                    // find unique name
                    if (methodNameMap.inverse().containsKey(name)) {
                        int i = 1;
                        String tmp;
                        do {
                            tmp = name + "_" + i;
                            i += 1;
                        } while (methodNameMap.inverse().containsKey(tmp));
                        name = tmp;
                    }

                    MethodRef methodRef = new MethodRef(cls.name, m.name,
                            m.desc);
                    methodNameMap.put(methodRef, name);

                    // determine the path infos to register under
                    MethodPathInfos pathInfos = ActionPathAnnotationUtil
                            .getPathInfos(m, () -> "/" + controllerName + "."
                                    + m.name);

                    // add the path infos for the method to the respective maps
                    if (Type.getArgumentTypes(m.desc).length == 0) {
                        // no parameters
                        for (String prefix : pathInfos.pathInfos) {
                            idx.registerPathInfo(prefix, new RequestParser() {
                                @Override
                                public RequestParseResult parse(HttpRequest req) {
                                    return createParseResult(createInvocation(
                                            instanceCls, methodRef));
                                }

                                @Override
                                public String toString() {
                                    return "MvcRequestParser[" + methodRef
                                            + "]";
                                };
                            });
                        }
                    } else {
                        // there are parameters
                        for (String prefix : pathInfos.pathInfos) {
                            String s = prefix + "/";

                            idx.registerPrefix(
                                    s,
                                    req -> createParseResult(parse(s,
                                            instanceCls, methodRef, req)));
                        }
                    }

                    methodToPrefixMap.put(Pair.of(instanceCls.name, methodRef),
                            pathInfos.primaryPathInfo);
                    baseMethodToPrefixMap.put(methodRef,
                            pathInfos.primaryPathInfo);
                }
            subclassMethods.addAll(addedMethods);
            cls = cache.tryGetNode(cls.superName).orElse(null);
        }

    }

    protected abstract RequestParseResult createParseResult(
            ActionInvocation<String> path);

    /**
     * Parse a request. The prefix must include the method name and the first
     * "/". The remaining pathInfo has the form
     * 
     * <pre>
     * ({argument}("/"{argument})*)?
     * </pre>
     */
    public ActionInvocation<String> parse(String prefix,
            ClassNode controllerClassNode, MethodRef methodRef,
            HttpRequest request) {
        return parse(prefix, controllerClassNode, methodRef, request,
                coreRequestInfo.getServletRequest().getSession().getId());
    }

    ActionInvocation<String> parse(String prefix,
            ClassNode controllerClassNode, MethodRef methodRef,
            HttpRequest request, String sessionId) {
        ActionInvocation<String> invocation;
        try {
            invocation = createInvocation(controllerClassNode, methodRef);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading "
                    + controllerClassNode.name + "." + methodRef.methodName
                    + "(" + methodRef.desc + ")", e);
        }
        Method method = invocation.methodInvocation.getMethod();
        boolean urlSign = shouldDoUrlSigning(method);

        Hasher hasher = null;
        byte[] hash = null;
        if (urlSign) {
            byte[] signature = Base64.getUrlDecoder().decode(
                    request.getParameter(SIGNATURE_PARAMETER_NAME));
            System.out.println("parsed sign: " + Arrays.toString(signature));
            hash = Arrays.copyOfRange(signature, coreConfig.urlSignatureBytes,
                    signature.length);

            hasher = Hashing.sha256().newHasher();
            urlSignatureHelper.hashSecret(hasher);
            hasher.putString(sessionId, Charsets.UTF_8);
            byte[] salt = Arrays.copyOfRange(signature, 0,
                    coreConfig.urlSignatureBytes);
            hasher.putBytes(salt);
            hasher.putString(prefix, Charsets.UTF_8);
        }

        String remaining = request.getPathInfo().substring(prefix.length(),
                request.getPathInfo().length());

        // collect arguments
        if (!remaining.isEmpty()) {
            int i = 0;
            for (String arg : Splitter.on('/').splitToList(remaining)) {
                invocation.methodInvocation.getArguments().add(arg);
                if (urlSign
                        && !method.getParameters()[i]
                                .isAnnotationPresent(UrlUnsigned.class)) {
                    hasher.putString("/" + arg, Charsets.UTF_8);
                }
                i++;
            }
        }

        // check url signature
        if (urlSign) {
            byte[] calculatedHash = Arrays.copyOfRange(hasher.hash().asBytes(),
                    0, coreConfig.urlSignatureBytes);
            if (!urlSignatureHelper.slowEquals(hash, calculatedHash)) {
                throw new RuntimeException("URL signature does not match");
            }
        }

        // collect parameters
        for (Entry<String, String[]> entry : request.getParameterMap()
                .entrySet()) {
            invocation.getParameters().put(entry.getKey(), entry.getValue());
        }
        return invocation;

    }

    /**
     * Create an {@link ActionInvocation} without parameters
     */
    protected ActionInvocation<String> createInvocation(
            ClassNode controllerClassNode, MethodRef methodRef) {
        ActionInvocation<String> invocation = new ActionInvocation<>();

        // load method
        try {
            Class<?> controllerClass = AsmUtil.loadClass(
                    Type.getObjectType(controllerClassNode.name),
                    coreConfig.dynamicClassLoader);
            Method method = AsmUtil.loadMethod(methodRef,
                    coreConfig.dynamicClassLoader);
            invocation.methodInvocation = new MethodInvocation<>(
                    controllerClass, method);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating invocation for "
                    + controllerClassNode.name + "." + methodRef.methodName, e);
        }
        return invocation;
    }

    @Override
    public UrlSpec generate(ActionInvocation<String> path, String sessionId) {
        Method method = path.methodInvocation.getMethod();
        boolean urlSign = shouldDoUrlSigning(method);

        Hasher hasher = null;
        byte[] salt = null;
        if (urlSign) {
            hasher = Hashing.sha256().newHasher();
            urlSignatureHelper.hashSecret(hasher);
            hasher.putString(sessionId, Charsets.UTF_8);
            salt = urlSignatureHelper.createSalt(coreConfig.urlSignatureBytes);
            hasher.putBytes(salt);
        }

        StringBuilder sb = new StringBuilder();
        MethodRef ref = MethodRef.of(method);
        String prefix = methodToPrefixMap.get(Pair.of(
                Type.getInternalName(path.methodInvocation.getInstanceClass()),
                ref));

        // try the base map
        if (prefix == null) {
            Collection<String> prefixes = baseMethodToPrefixMap.get(ref);
            if (prefixes.size() == 1)
                prefix = Iterables.getOnlyElement(prefixes);
        }

        if (prefix == null)
            throw new RuntimeException("Unable to find prefix for\n" + ref
                    + "\nfor instance class "
                    + path.methodInvocation.getInstanceClass().getName());
        sb.append(prefix);
        if (urlSign) {
            hasher.putString(prefix, Charsets.UTF_8);
        }

        // add arguments
        for (int i = 0; i < method.getParameterCount(); i++) {
            String argument = "/" + path.methodInvocation.getArguments().get(i);
            sb.append(argument);

            if (urlSign
                    && !method.getParameters()[i]
                            .isAnnotationPresent(UrlUnsigned.class)) {
                hasher.putString(argument, Charsets.UTF_8);
            }
        }

        ArrayList<Pair<String, String>> parameters = new ArrayList<>();
        for (Entry<String, String[]> entry : path.getParameters().entrySet()) {
            for (String value : entry.getValue()) {
                parameters.add(Pair.of(entry.getKey(), value));
            }
        }

        if (urlSign) {
            int length = coreConfig.urlSignatureBytes;
            byte[] hash = hasher.hash().asBytes();
            byte[] signature = new byte[length * 2];
            System.arraycopy(salt, 0, signature, 0, length);
            System.arraycopy(hash, 0, signature, length, length);
            System.out.println("gen sign: " + Arrays.toString(signature));
            parameters.add(Pair.of(SIGNATURE_PARAMETER_NAME, Base64
                    .getUrlEncoder().encodeToString(signature)));
        }
        return new UrlSpec(new PathInfo(sb.toString()), parameters);

    }

    private boolean shouldDoUrlSigning(Method method) {
        return coreConfig.doUrlSigning
                && !method.isAnnotationPresent(UrlUnsigned.class);
    }
}
