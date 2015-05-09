package com.github.ruediste.rise.core.front.reload;

import org.objectweb.asm.ClassReader;

public class ReloadConfiguration {
	/**
	 * Flags to be used when calling
	 * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} for class
	 * change notification.
	 */
	public int classScanningFlags = ClassReader.SKIP_CODE
			+ ClassReader.SKIP_DEBUG;

	public long fileChangeSettleDelayMs = 10;
}
