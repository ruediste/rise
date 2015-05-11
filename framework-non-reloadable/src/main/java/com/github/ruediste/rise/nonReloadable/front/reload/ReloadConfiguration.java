package com.github.ruediste.rise.nonReloadable.front.reload;

import org.objectweb.asm.ClassReader;

import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier.FileChangeTransaction;

public class ReloadConfiguration {
	/**
	 * Flags to be used when calling
	 * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} for class
	 * change notification.
	 */
	public int classScanningFlags = ClassReader.SKIP_CODE
			+ ClassReader.SKIP_DEBUG;

	/**
	 * Delay in ms to wait after a file change is detected until the
	 * corresponding event is raised. Allows multiple changes to be sent in one
	 * {@link FileChangeTransaction}.
	 */
	public long fileChangeSettleDelayMs = 10;
}
