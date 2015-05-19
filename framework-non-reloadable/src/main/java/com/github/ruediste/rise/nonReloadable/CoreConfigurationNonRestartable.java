package com.github.ruediste.rise.nonReloadable;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.ClassReader;

import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.salta.standard.Stage;

@Singleton
public class CoreConfigurationNonRestartable {
	@Inject
	Stage stage;

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

	/**
	 * controls if the the schema migration using Flyway scripts should be run
	 * during startup. Depends on the {@link Stage} if not defined (the
	 * default).
	 */
	public Optional<Boolean> runSchemaMigration = Optional.empty();

	public boolean isRunSchemaMigration() {
		return runSchemaMigration.orElse(stage != Stage.DEVELOPMENT);
	}

	/**
	 * controls if the db drop-and-create functionality is enabled. Depends on
	 * the {@link Stage} if not defined (the default).
	 */
	public Optional<Boolean> dbDropAndCreateEnabled = Optional.empty();

	public boolean isDbDropAndCreateEnabled() {
		return dbDropAndCreateEnabled.orElse(stage == Stage.DEVELOPMENT);
	}

	public long restartQueryTimeout = 30000;
}
