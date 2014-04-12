package laf.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import laf.module.model.ProjectModel;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class CheckModulesMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.outputDirectory}", property = "outputDir", required = true, readonly = true)
	private File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Checking Modules " + outputDirectory.toString());

		final ClassParser parser = new ClassParser();
		try {
			Files.walkFileTree(outputDirectory.toPath(),
					new FileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(Path dir,
								BasicFileAttributes attrs) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							if (attrs.isRegularFile()
									&& file.getFileName().toString()
											.endsWith(".class")) {
								getLog().info(file.toString());
								try {
							parser.parse(file.toFile());
								} catch (Throwable t) {
									getLog().error(
									"Error while parsing " + file, t);
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file,
								IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir,
								IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}
					});
		} catch (IOException e) {
			throw new RuntimeException("Error while reading input files", e);
		}

		ProjectModel project = parser.getProject();
		project.resolveDependencies();
		getLog().info(project.details());
		List<String> errors = project.checkClasses();
		if (!errors.isEmpty()) {
			getLog().error("Errors while checking module dependencies");
			for (String s : errors) {
				getLog().error(s);
			}
			throw new RuntimeException(
					"Error while checking module dependencies. See log for details");
		}
		getLog().info("Module dependencies checked");
	}

}
