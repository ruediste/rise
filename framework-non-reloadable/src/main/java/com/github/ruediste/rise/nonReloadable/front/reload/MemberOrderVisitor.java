package com.github.ruediste.rise.nonReloadable.front.reload;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MemberOrderVisitor extends ClassVisitor {
    private final List<String> members = new ArrayList<>();

    public MemberOrderVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        getMembers().add("F" + name + ";" + desc);
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        getMembers().add("M" + name + ";" + desc);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public List<String> getMembers() {
        return members;
    }
}