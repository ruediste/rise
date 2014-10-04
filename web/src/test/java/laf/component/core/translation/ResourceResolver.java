package laf.component.core.translation;

import java.util.Locale;

public interface ResourceResolver {

	Object resolve(String key, Locale locale);
}
