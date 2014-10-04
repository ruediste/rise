package laf.component.core.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PrimitiveIterator.OfInt;

import javax.inject.Inject;

import laf.core.base.Val;

/**
 * Formatter for {@link TString}s which closely follows the semantics of
 * {@link MessageFormat}.
 *
 * <p>
 * The main differences from MessageFormat are:
 * <ul>
 * <li>Escaping with hashes instead of single colons</li>
 * <li>parameters are identified by string keys instead of numbers</li>
 * </ul>
 * </p>
 */
public class TStringFormatter {

	@Inject
	ResourceResolver resourceResolver;

	public Object format(TString tString, Locale locale) {

		Object templateObj = resourceResolver.resolve(tString.getResourceKey(),
				locale);
		if (templateObj == null) {
			throw new RuntimeException("No resource found for key "
					+ tString.getResourceKey());
		}
		if (!(templateObj instanceof String)) {
			throw new RuntimeException(
					"Resolved template is not a string. Locale: " + locale
							+ " key: " + tString.getResourceKey()
							+ " resolved resource: " + templateObj);
		}
		String template = (String) templateObj;

		StringBuilder result = new StringBuilder();

		OfInt it = template.codePoints().iterator();
		while (it.hasNext()) {
			int c = it.nextInt();
			if (c == '#') {
				if (it.hasNext()) {
					result.appendCodePoint(it.nextInt());
				}
			} else if (c == '{') {
				int placeholderDepth = 1;
				StringBuilder placeholder = new StringBuilder();
				// read placeholder
				placeholderLoop: while (it.hasNext()) {
					c = it.nextInt();
					if (c == '#') {
						// the placeholder keeps the escaping
						placeholder.appendCodePoint(c);
						if (it.hasNext()) {
							placeholder.appendCodePoint(it.nextInt());
						}
					} else if (c == '}') {
						if (placeholderDepth == 0) {
							throw new RuntimeException(
									"Error while parsing template: too many closing braces. Template: "
											+ template);
						}
						if (placeholderDepth == 1) {
							// we are closing the placeholder, resolve it
							resolvePlaceholder(result, tString,
									placeholder.toString());
							break placeholderLoop;
						}
						placeholderDepth--;
					} else {
						placeholder.appendCodePoint(c);
					}
				}
			} else {
				result.appendCodePoint(c);
			}
		}
		return result.toString();
	}

	private void resolvePlaceholder(StringBuilder result, TString tString,
			String placeholder) {
		Val<Object> parameter = tString.getParameter(placeholder);
		if (parameter == null) {
			throw new RuntimeException("Unknown parameter " + placeholder
					+ " in string " + tString);
		}
		result.append(parameter.get());
	}

}
