package laf.component.core.translation;

import java.util.Locale;

/**
 * Interface for resolving Resources
 */
public interface ResourceResolver {
	Object resolve(String key, Locale locale);
}
