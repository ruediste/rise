package laf.component.core.translation;

import java.util.Locale;

public interface FormatHandler {

	public String handle(Locale locale, Object parameter, String style);
}
