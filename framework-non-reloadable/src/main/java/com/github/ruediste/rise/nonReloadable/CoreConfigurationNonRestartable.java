package com.github.ruediste.rise.nonReloadable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.ClassReader;

import com.github.ruediste.rise.nonReloadable.front.DefaultStartupErrorHandler;
import com.github.ruediste.rise.nonReloadable.front.StartupErrorHandler;
import com.github.ruediste.rise.nonReloadable.front.reload.FileChangeNotifier.FileChangeTransaction;
import com.github.ruediste.salta.standard.Stage;
import com.google.common.base.Strings;
import com.google.common.reflect.Reflection;

@Singleton
@NonRestartable
public class CoreConfigurationNonRestartable {
	@Inject
	Stage stage;

	/**
	 * Flags to be used when calling
	 * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, int)} for class
	 * change notification.
	 */
	public int classScanningFlags = ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG;

	/**
	 * Delay in ms to wait after a file change is detected until the
	 * corresponding event is raised. Allows multiple changes to be sent in one
	 * {@link FileChangeTransaction}.
	 */
	public long fileChangeSettleDelayMs = 500;

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

	private StartupErrorHandler startupEventHandler;

	public void handleError(Throwable t, HttpServletRequest request, HttpServletResponse response) {
		startupEventHandler.handle(t, request, response);
	}

	public Supplier<StartupErrorHandler> startupEventHandlerSupplier;

	@PostConstruct
	void postConstruct(Provider<DefaultStartupErrorHandler> errorHandler) {
		startupEventHandlerSupplier = () -> errorHandler.get();
	}

	public void initialize() {
		startupEventHandler = startupEventHandlerSupplier.get();
		if (!Strings.isNullOrEmpty(basePackage)) {
			scannedPrefixes.add(basePackage);
			if (stackTraceFilter == null)
				if (stage == Stage.DEVELOPMENT)
					stackTraceFilter = new DefaultStrackTraceFilter(
							e -> e.getClassName() == null || e.getClassName().startsWith(basePackage));
				else
					stackTraceFilter = e -> {
					};
		}

	}

	public long restartQueryTimeout = 30000;

	public final Set<String> scannedPrefixes = new HashSet<String>();

	{
		scannedPrefixes.add("com/github/ruediste/rise/");
		scannedPrefixes.add("assets/");
		scannedPrefixes.add("java/lang/Object.class");
	}

	public void addScannedPackage(Class<?> cls) {
		scannedPrefixes.add(Reflection.getPackageName(cls).replace('.', '/'));
	}

	public boolean shouldClasspathResourceBeScanned(String resourceName) {
		return scannedPrefixes.stream().anyMatch(prefix -> resourceName.startsWith(prefix));
	}

	public String basePackage = "";

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
		scannedPrefixes.add(basePackage.replace('.', '/'));
	}

	/**
	 * set {@link #basePackage} to the package of the given class
	 */
	public void setBasePackage(Class<?> clazz) {
		setBasePackage(Reflection.getPackageName(clazz));
	}

	/**
	 * The stack trace filter is used to omit stack frames which are not part of
	 * the application code (see {@link StackTraceFilter}).
	 * 
	 * <p>
	 * If this field is left {@code null}, a default filter which filters stack
	 * frames which are not within the {@link #basePackage} is installed in
	 * {@link ApplicationStage#DEVELOPMENT DEVELOPMENT} stage. In the other
	 * stages no filtering is performed by default.
	 */
	public StackTraceFilter stackTraceFilter;

	public void disableStackTraceFiltering() {
		stackTraceFilter = e -> {
		};
	}

	public StackTraceFilter getStackTraceFilter() {
		if (stackTraceFilter == null)
			return e -> {
			};
		return stackTraceFilter;
	}

	public final Set<Class<?>> additionalScannedClasses = new HashSet<>();
	{
		additionalScannedClasses.add(Object.class);
	}

	public Collection<Class<?>> getAdditionalScannedClasses() {
		return additionalScannedClasses;
	}

}
