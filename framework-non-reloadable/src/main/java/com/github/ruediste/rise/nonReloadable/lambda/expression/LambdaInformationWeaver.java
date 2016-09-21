package com.github.ruediste.rise.nonReloadable.lambda.expression;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.CapturingLambda;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;

public class LambdaInformationWeaver extends ClassVisitor {

    private String className;

    public LambdaInformationWeaver(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodNode mn = new MethodNode(access, name, desc, null, null);
        MethodVisitor next = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM5, mn) {

            @Override
            public void visitEnd() {
                // Two Step Algorithm: First we determine how the result of each
                // invokedynamic instruciton is used (in what parameters).
                // Then we modify the invokedynamic instructions, taking the
                // information of how the result is used into account.
                Frame<SourceValue>[] frames;
                try {
                    frames = new Analyzer<>(new SourceInterpreter()).analyze(className, mn);
                } catch (AnalyzerException e) {
                    throw new RuntimeException("Error while analyzing method", e);
                }

                AbstractInsnNode[] instructions = mn.instructions.toArray();

                // a map containing all invokedynamic instructions of the
                // current method, along with information about the parameter of
                // the instruction
                // using the result of the invokedynamic
                Map<InvokeDynamicInsnNode, Set<UsingParameter>> invokeDynamicInstructions = new HashMap<>();

                // iterate all instructions, process method invocations
                for (int idx = 0; idx < frames.length; idx++) {
                    Frame<SourceValue> frame = frames[idx];
                    if (frame == null)
                        continue;
                    AbstractInsnNode instruction = instructions[idx];

                    // filter method invocations
                    int tag = -1;
                    switch (instruction.getOpcode()) {
                    case Opcodes.INVOKEINTERFACE:
                        tag = Opcodes.H_INVOKEINTERFACE;
                        break;
                    case Opcodes.INVOKESTATIC:
                        tag = Opcodes.H_INVOKESTATIC;
                        break;
                    case Opcodes.INVOKEVIRTUAL:
                        tag = Opcodes.H_INVOKEVIRTUAL;
                        break;
                    case Opcodes.INVOKESPECIAL:
                        tag = Opcodes.H_INVOKESPECIAL;
                        break;
                    }

                    // no method invocation, continue
                    if (tag == -1) {
                        continue;
                    }

                    MethodInsnNode methodInsn = (MethodInsnNode) instruction;

                    // adjust tag for constructor invocations
                    if (methodInsn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        if ("<init>".equals(methodInsn.name))
                            tag = Opcodes.H_NEWINVOKESPECIAL;
                    }

                    // iterate method parameters
                    Type[] argumentTypes = Type.getArgumentTypes(methodInsn.desc);
                    for (int i = 0; i < argumentTypes.length; i++) {
                        // iterate source instructions of the parameter
                        for (AbstractInsnNode sourceInstruction : frame
                                .getStack((frame.getStackSize() - argumentTypes.length) + i).insns) {
                            if (sourceInstruction.getOpcode() == Opcodes.INVOKEDYNAMIC) {
                                InvokeDynamicInsnNode invokeDynamic = (InvokeDynamicInsnNode) sourceInstruction;
                                invokeDynamicInstructions.computeIfAbsent(invokeDynamic, x -> new HashSet<>())
                                        .add(new UsingParameter(
                                                new Handle(tag, methodInsn.owner, methodInsn.name, methodInsn.desc),
                                                i));
                            }
                        }
                    }
                }

                // iterate invoke dynamic instructions
                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() != Opcodes.INVOKEDYNAMIC)
                        continue;
                    InvokeDynamicInsnNode insn = (InvokeDynamicInsnNode) instruction;

                    // only process invocations of the lambda meta factory
                    Handle bsm = insn.bsm;
                    if (bsm.getTag() != Opcodes.H_INVOKESTATIC
                            || !"java/lang/invoke/LambdaMetafactory".equals(bsm.getOwner())
                            || !"metafactory".equals(bsm.getName())) {
                        continue;
                    }

                    Set<UsingParameter> usingParameters = invokeDynamicInstructions.get(insn);
                    if (usingParameters != null && usingParameters.size() == 1) {
                        UsingParameter usingParameter = usingParameters.iterator().next();
                        // switch to custom meta factory with using parameter
                        // information
                        insn.bsm = new Handle(Opcodes.H_INVOKESTATIC,
                                Type.getInternalName(LambdaInformationWeaver.class), "metafactory2",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;I)Ljava/lang/invoke/CallSite;");
                        Object[] newArgs = Arrays.copyOf(insn.bsmArgs, 5);
                        newArgs[3] = usingParameter.usingMethod;
                        newArgs[4] = usingParameter.paramIndex;
                        insn.bsmArgs = newArgs;
                    } else {
                        // switch to custom meta factory without using parameter
                        // information
                        insn.bsm = new Handle(Opcodes.H_INVOKESTATIC,
                                Type.getInternalName(LambdaInformationWeaver.class), "metafactory",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;");

                    }

                }

                mn.accept(next);
            }
        };
    }

    /**
     * Custom metafactory to capture lambda information. This version does not
     * get information about the method parameter using the result
     */
    public static CallSite metafactory(MethodHandles.Lookup caller, String invokedName, MethodType invokedType,
            /* argarray: */MethodType samMethodType, MethodHandle implMethod, MethodType instantiatedMethodType)
                    throws LambdaConversionException, NoSuchMethodException, IllegalAccessException {

        CallSite result = LambdaMetafactory.metafactory(caller, invokedName, invokedType, samMethodType, implMethod,
                instantiatedMethodType);
        Class<?> lambdaInterface = invokedType.returnType();
        if (CapturingLambda.class.isAssignableFrom(lambdaInterface)) {
            result = doWrap(samMethodType, implMethod, result);
        }
        return result;
    }

    /**
     * Custom metafactory to capture lambda information. This version get's
     * passed information about the parameter using the created value.
     */
    public static CallSite metafactory2(MethodHandles.Lookup caller, String invokedName, MethodType invokedType,
            /* argarray: */MethodType samMethodType, MethodHandle implMethod, MethodType instantiatedMethodType,
            MethodHandle usingMethodHandle, int usingParameterIndex)
                    throws LambdaConversionException, NoSuchMethodException, IllegalAccessException {

        CallSite result = LambdaMetafactory.metafactory(caller, invokedName, invokedType, samMethodType, implMethod,
                instantiatedMethodType);

        // check capturing lambda interface
        Class<?> lambdaInterface = invokedType.returnType();
        boolean doWrap = CapturingLambda.class.isAssignableFrom(lambdaInterface);

        if (!doWrap) {
            // check Capture annotation on parameters of the method using the
            // lambda object
            Executable usingMethod = MethodHandles.reflectAs(Executable.class, usingMethodHandle);
            doWrap = usingMethod.getParameters()[usingParameterIndex].isAnnotationPresent(Capture.class);
        }
        if (doWrap) {
            result = doWrap(samMethodType, implMethod, result);
        }
        return result;
    }

    private static ConstantCallSite doWrap(MethodType samMethodType, MethodHandle implMethod, CallSite innerCallsite)
            throws NoSuchMethodException, IllegalAccessException {
        MethodHandle lambdaHandle = innerCallsite.dynamicInvoker();
        Member calledMethod = MethodHandles.reflectAs(Member.class, implMethod);
        Class<?> lambdaInterface = lambdaHandle.type().returnType();

        MethodHandle wrapHandle = MethodHandles.publicLookup()
                .findStatic(LambdaInformationWeaver.class, "wrap",
                        MethodType.methodType(Object.class, MethodType.class, Member.class, Class.class,
                                MethodHandle.class, Object[].class))
                .bindTo(samMethodType).bindTo(calledMethod).bindTo(lambdaInterface).bindTo(lambdaHandle)
                .asCollector(Object[].class, lambdaHandle.type().parameterCount()).asType(lambdaHandle.type());

        ConstantCallSite tmp = new ConstantCallSite(wrapHandle);
        return tmp;
    }

    private static class InfoInvocationHandler implements InvocationHandler {

        private final Member member;
        private final Object lambda;
        private final Object[] capturedArgs;
        private final MethodType samMethodType;

        public InfoInvocationHandler(MethodType samMethodType, Member member, Object lambda, Object[] capturedArgs) {
            this.samMethodType = samMethodType;
            this.member = member;
            this.lambda = lambda;
            this.capturedArgs = capturedArgs;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(lambda, args);
        }

        public Member getMember() {
            return member;
        }

    }

    public static LambdaExpression<?> getLambdaExpression(Object lambda) {
        if (!Proxy.isProxyClass(lambda.getClass()))
            throw new RuntimeException("No lambda information was captured for the supplied lambda object");
        InfoInvocationHandler handler = (InfoInvocationHandler) Proxy.getInvocationHandler(lambda);
        return new ExpressionClassCracker().parseLambdaMethod(handler.samMethodType, handler.member,
                handler.capturedArgs);
    }

    public static Object wrap(MethodType samMethodType, Member member, Class<?> lambdaInterface,
            MethodHandle lambdaHandle, Object[] args) throws Throwable {
        Object lambda = lambdaHandle.asSpreader(Object[].class, args.length).invoke(args);
        return Proxy.newProxyInstance(lambdaInterface.getClassLoader(), new Class<?>[] { lambdaInterface },
                new InfoInvocationHandler(samMethodType, member, lambda, args));
    }

    private static class UsingParameter {
        Handle usingMethod;
        int paramIndex;

        public UsingParameter(Handle usingMethod, int paramIndex) {
            super();
            this.usingMethod = usingMethod;
            this.paramIndex = paramIndex;
        }

    }

}
