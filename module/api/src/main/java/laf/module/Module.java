package laf.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Module is defined by putting this annotation to a class, which represents
 * the module.
 * 
 * <p>
 * By default all classes in the package of the module representing class are
 * part of the module, excluding sub packages.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Module {

	/**
	 * List of modules this module depends on. These modules are not exported.
	 * Classes of this module may only use classes from modules this module
	 * depends on.
	 */
	Class<?>[] depends() default {};

	/**
	 * List of modules which are exported to clients of this module. All listed
	 * modules are as well regarded as dependencies of this module. Exports are
	 * transitive. If module A exports module B and module B exports module C,
	 * clients of A also have a dependency on C.
	 */
	Class<?>[] exports() default {};

	/**
	 * Additional classes to include
	 */
	Class<?>[] include() default {};

	/**
	 * Additional packages to include. If a package starts with a dot, it is
	 * interpreted relative to the current package. If it ends with a star, all
	 * sub packages are included (use ".*" to include all sub packages of the 
	 * current package)
	 */
	String[] includePackage() default {};

	/**
	 * Classes to exclude
	 */
	Class<?>[] exclude() default {};

	/**
	 * Packages to exclude. If a package starts with a dot, it is interpreted
	 * relative to the current package.
	 */
	String[] excludePackage() default {};
}
