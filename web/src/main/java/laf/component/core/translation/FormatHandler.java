package laf.component.core.translation;

import java.util.Locale;

/**
 * Interface for formatting placeholders in TStrings
 */
public interface FormatHandler {

	public String handle(Locale locale, Object parameter, String style,
			TStringFormatter formatter, TString tString);
}
