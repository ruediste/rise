package com.github.ruediste.laf.core.front.reload;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.signature.SignatureVisitor;

public class PrintingSignatureVisitor extends SignatureVisitor {

	private String name;

	public PrintingSignatureVisitor(String name) {
		super(ASM5);
		this.name = name;
	}

	@Override
	public void visitFormalTypeParameter(String name) {
		System.out.println(this.name + "> visitFormalTypeParameter " + name);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		System.out.println(name + "> visitClassBound ");
		return new PrintingSignatureVisitor(name + " visitClassBound");
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		System.out.println(name + "> visitInterfaceBound ");
		return new PrintingSignatureVisitor(name + " visitInterfaceBound");
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		System.out.println(name + "> visitSuperclass ");
		return new PrintingSignatureVisitor(name + " visitSuperclass");
	}

	@Override
	public SignatureVisitor visitInterface() {
		System.out.println(name + "> visitInterface ");
		return new PrintingSignatureVisitor(name + " visitInterface");
	}

	@Override
	public SignatureVisitor visitParameterType() {
		System.out.println(name + "> visitParameterType ");
		return new PrintingSignatureVisitor(name + " visitParameterType");
	}

	@Override
	public SignatureVisitor visitReturnType() {
		System.out.println(name + "> visitReturnType ");
		return new PrintingSignatureVisitor(name + " visitReturnType");
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		System.out.println(name + "> visitExceptionType ");
		return new PrintingSignatureVisitor(name + " visitExceptionType");
	}

	@Override
	public void visitBaseType(char descriptor) {
		System.out.println(name + "> visitBaseType ");
	}

	@Override
	public void visitTypeVariable(String name) {
		System.out.println(this.name + "> visitTypeVariable " + name);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		System.out.println(this.name + "> visitArrayType ");
		return new PrintingSignatureVisitor(this.name + " visitArrayType");
	}

	@Override
	public void visitClassType(String name) {
		System.out.println(this.name + "> visitClassType " + name);
	}

	@Override
	public void visitInnerClassType(String name) {
		System.out.println(this.name + "> visitInnerClassType");
	}

	@Override
	public void visitTypeArgument() {
		System.out.println(name + "> visitTypeArgument");
	}

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		System.out.println(name + "> visitTypeArgument wildcard:" + wildcard);
		return new PrintingSignatureVisitor(name + " visitTypeArgument");
	}

	@Override
	public void visitEnd() {
		System.out.println(name + "> visitEnd");
	}

}
