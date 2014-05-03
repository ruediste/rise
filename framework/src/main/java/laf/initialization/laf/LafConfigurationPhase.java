package laf.initialization.laf;

import laf.initialization.InitializationPhase;
import laf.initialization.Initializer;

/**
 * {@link InitializationPhase} during which the framework is configured.
 *
 * <p>
 * {@link Initializer}s in this phase should generally execute quickly, since
 * later initializers might make initializations obsolete.
 * </p>
 */
public interface LafConfigurationPhase extends InitializationPhase {

}
