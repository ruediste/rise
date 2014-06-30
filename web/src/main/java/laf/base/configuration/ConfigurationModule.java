package laf.base.configuration;

import laf.base.BaseModuleImpl;

import org.jabsaw.Module;

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
 * <strong> Accessing Configuration Values </strong> <br/>
 * To retrieve and set configuration values in a typesafe way, each
 * configuration value has to have a java representation. We chose to use a type
 * for that. More precise, for each configuration value an interface extending
 * {@link ConfigurationParameter} has to be defined:
 *
 * <pre>
 * public interface UserName extends ConfigurationValue&lt;String&gt; {
 * }
 * </pre>
 *
 * To access the configuration value, an instance of the defined interface has
 * to be injected:
 *
 * <pre>
 * {@literal @}Inject
 * UserName userName;
 * </pre>
 *
 * The value can the be accessed using {@code userName.get()}.
 *
 * <strong> Defining Configuration Values </strong> <br/>
 * Configuration values are either defined in java classes or in .properties
 * files. In java, values are defined in a separate class, containing methods
 * which accept a {@link ConfigurationParameter} argument. In the method body, the
 * configuration value is set:
 *
 * <pre>
 * void defineUserName(UserName userName){
 *   userName.set("Frank")
 * }
 * </pre>
 *
 * For the properties file, each configuration value has one or more keys. By
 * default, the only key is the fully qualified class name of the configuration
 * value interface. This key can optionally prepended by further keys using the
 * {@link ConfigurationKey} annotation. When determining the configuration
 * value, the .properties file is searched for the first matching key.
 *
 * <p>
 * Each application has to define a subclass of {@link ConfigurationFactory}
 * . The base class contains a provider method for {@link ConfigurationParameter}s.
 * By default the .properties file "configuration.properties" is used first, and
 * then the values defined within the subclass of ConfigurationFactoryBase. This
 * can be changed by overriding
 * {@link ConfigurationFactory#registerConfigurationValueProviders()}.
 * </p>
 *
 * <strong> Specifying Values in .properties Files</strong> <br/>
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
public class ConfigurationModule {

}
