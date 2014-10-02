package laf.core.web.resource;

import org.jabsaw.Module;

/**
 * Module which serves and transforms web resources.
 *
 * *
 * <p>
 * Most HTML pages use various CSS and JavaScript resources. After loading a
 * html page, the browser loads all referenced resources to complete page
 * loading.
 * </p>
 *
 * </p> To reduce page loading times, it is preferable to concatenate multiple
 * CSS and JavaScript resources and remove comments and whitespace as far as
 * possible. This reduces the amount of data which has to be transferred and the
 * number of time consuming server requests. In addition, the source of a CSS or
 * JavaScript resource is written in a different language which is transformed
 * or compiled. </p>
 *
 * <p>
 * These transformations can either performed at build time or at run time.
 * Runtime transformation was chosen since it removes the need of maintaining a
 * complex build system.
 * </p>
 *
 */
@Module(description = "Serve and transform static web resources (CSS, JavaScript)", imported = {
		laf.core.http.CoreHttpModule.class,
		laf.core.requestParserChain.CoreRequestParserChainModule.class })
public class StaticWebResourceModule {

}
