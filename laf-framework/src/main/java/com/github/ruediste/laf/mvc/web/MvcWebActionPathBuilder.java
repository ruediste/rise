package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.laf.mvc.MvcActionPathBuilder;

/**
 * Builder to create {@link ActionInvocation}s for controller method invocations
 * using a fluent interface. Example:
 *
 * <pre>
 * <code>
 * ActionPathBuilder builder=instance.get();
 * builder.initialize();
 * ActionPath path= builder.
 * 			.set(property, 27)
 * 			.controller(TestController.class).actionMethod(5);
 * </code>
 * </pre>
 *
 */
public class MvcWebActionPathBuilder extends MvcActionPathBuilder {

}