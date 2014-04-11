package laf.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class CheckModulesMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.outputDirectory}", property = "outputDir", required = true, readonly = true)
	private File outputDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Checking Modules "+outputDirectory.toString());
		try {
			Files.walkFileTree(outputDirectory.toPath(), new FileVisitor<Path>() {

				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					if (attrs.isRegularFile() && file.getFileName().toString().endsWith(".class")){
						getLog().info(file.toString());
						System.out.println(file);
					}
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Error while reading input files", e);
		}
	}

}
