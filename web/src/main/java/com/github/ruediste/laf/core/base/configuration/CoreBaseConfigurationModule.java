package com.github.ruediste.laf.core.base.configuration;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.base.BaseModuleImpl;

/**
 * Typesafe Configuration System supporting properties files.
 *
 * <p>
 * Although the Light Application Framework comes with sensible defaults, at
 * some point these defaults will not fit and the framework needs to be
 * configured. It turns out that the requirements for the configuration of
 * business applications are very similar. Thus this Configuration System can be
 * used for both tasks.
 * </p>
 *
 * <strong> Requirements </strong> <br/>
 * <ul>
 * <li>the configuration can be set in a typesafe way from java code</li>
 * <li>the configuration can be overridden using .properties files</li>
 * <li>configured values can be retrieved in a typesafe way</li>
 * <li>multiple configuration sources can be combined</li>
 * </ul>
 *
 * <strong> Declaring Configuration Parameters </strong> <br/>
 *  
 * To retrieve and set configuration values in a typesafe way, each
 * configuration value has to have a java representation. We chose to use a type
 * for that. More precise, for each configuration parameter an interface extending
 * {@link ConfigurationParameter} has to be defined:
 *
 * <pre>
 * public interface UserName extends ConfigurationParameter&lt;String&gt; {
 * }
 * </pre>
 *
 * <strong> Accessing Configuration Values </strong> <br/>
 * To access the configuration value, an instance of the defined interface has
 * to be injected:
 *
 * <pre>
 * {@literal @}Inject
 * ConfigurationValue&lt;UserName&gt; userName;
 * </pre>
 *
 * The value can the be accessed using {@code userName.value().get()}.
 *
 * <strong> Defining Configuration Values </strong> <br/>
 * <p>
 * When the first {@link ConfigurationValue} is accessed (typically during application startup),
 * the {@link DiscoverConfigruationEvent} is raised. The application has to observe this event and
 * register some {@link ConfigurationValueProvider}s. Providers registered later override earlier
 * providers.  
 * </p>
 * 
 * <p>
 * Configuration values can be defined by creating a class extending from {@link ConfigurationDefiner}.
 * The class defines values for {@link ConfigurationParameter}s by declaring methods
 * with parameter interface as argument. In the method body, the
 * configuration value must be set:
 * </p>
 *
 * <pre>
 * void define(UserName userName){
 *   userName.set("Frank")
 * }
 * </pre>
 * 
 * <p>
 * An instance of such a class can be registered as {@link ConfigurationValueProvider} by calling
 * {@link DiscoverConfigruationEvent#add(ConfigurationDefiner)}
 * </p>
 * 
 * <p>
 * To modify the value of an earlier provider, the define method can be annotate with {@link ExtendConfiguration}.
 * The existing configuration value can be accessed using {@link ConfigurationParameter#get()}
 * </p>
 *
 * <p>
 * Properties files are registered using {@link DiscoverConfigruationEvent#addPropretiesFile(String)}.
 * Each configuration parameter has one or more keys. By
 * default, the only key is the fully qualified class name of the configuration
 * parameter interface. This key can optionally prepended by further keys using the
 * {@link ConfigurationKey} annotation. When determining the configuration
 * value, the .properties file is searched for the first matching key.
 *</p>
 *
 * <p>
 * Strings and primitives are be parsed using the respective valueOf method.
 * Class instances can be defined by their fully qualified class names. The
 * instances are obtained from CDI. For some common collection types, the
 * elements can be declared as comma separated list. In the case of string
 * lists, the comma has to be escaped using \,
 * </p>
 * <p>
 * In all cases the fully qualified name of a subclass of
 * {@link ConfigurationValueFactory} can be given, appending .factory to the
 * key. The factory is instantiated using CDI and the result of the
 * {@link ConfigurationValueFactory#getValue()} is used.
 * </p>
 */
@Module(description = "Typsafe Configuration System supporting .properties files", hideFromDependencyGraphOutput = true, imported = { BaseModuleImpl.class })
public class CoreBaseConfigurationModule {

}
