package laf.component.core.translation;

import java.text.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.*;
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
 * <li>Escaping with dollar signs instead of single colons</li>
 * <li>parameters are identified by string keys instead of numbers</li>
 * </ul>
 * </p>
 */
public class TStringFormatter {

	@Inject
	ResourceResolver resourceResolver;
	private Map<String, FormatHandler> handlers;

	public String format(TString tString, Locale locale) {

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

		return formatTemplate(template, tString, locale);
	}

	private String formatTemplate(String template, TString tString,
			Locale locale) {
		StringBuilder result = new StringBuilder();

		OfInt it = template.codePoints().iterator();
		while (it.hasNext()) {
			int c = it.nextInt();
			if (c == '$') {
				if (it.hasNext()) {
					result.appendCodePoint(it.nextInt());
				}
			} else if (c == '{') {
				int placeholderDepth = 1;
				StringBuilder placeholder = new StringBuilder();
				// read placeholder
				placeholderLoop: while (it.hasNext()) {
					c = it.nextInt();
					if (c == '$') {
						// the placeholder keeps the escaping
						placeholder.appendCodePoint(c);
						if (it.hasNext()) {
							placeholder.appendCodePoint(it.nextInt());
						}
					} else if (c == '{') {
						placeholder.appendCodePoint(c);
						placeholderDepth++;
					} else if (c == '}') {
						if (placeholderDepth == 0) {
							throw new RuntimeException(
									"Error while parsing template: too many closing braces. Template: "
											+ template);
						}
						if (placeholderDepth == 1) {
							// we are closing the placeholder, resolve it
							resolvePlaceholder(locale, result, tString,
									placeholder.toString());
							break placeholderLoop;
						}
						placeholder.appendCodePoint(c);
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

	private void resolvePlaceholder(Locale locale, StringBuilder result,
			TString tString, String placeholder) {
		ArrayList<StringBuilder> builders = new ArrayList<>(3);
		StringBuilder sb = new StringBuilder();

		OfInt it = placeholder.codePoints().iterator();
		while (it.hasNext()) {
			int c = it.nextInt();
			if (c == '$') {
				// the style keeps the escaping
				if (builders.size() > 1) {
					sb.appendCodePoint(c);
				}
				if (it.hasNext()) {
					sb.appendCodePoint(it.nextInt());
				}
			} else if (c == ',' && builders.size() < 2) {
				builders.add(sb);
				sb = new StringBuilder();
			} else {
				sb.appendCodePoint(c);
			}
		}
		builders.add(sb);

		if (builders.size() == 0) {
			throw new RuntimeException("no parameter key found in "
					+ placeholder);
		}
		if (builders.size() > 3) {
			throw new RuntimeException("too may commas in " + placeholder
					+ " tString: " + tString);
		}

		String parameterKey = builders.get(0).toString().trim();

		Val<Object> parameter = tString.getParameter(parameterKey);
		if (parameter == null) {
			throw new RuntimeException("Unknown parameter " + parameterKey
					+ " in string " + tString);
		}

		if (builders.size() == 1) {
			result.append(parameter.get());
		} else {
			String format = builders.get(1).toString().trim();
			FormatHandler handler = handlers.get(format);
			if (handler == null) {
				throw new RuntimeException("Unknow format " + format
						+ " in placeholder " + placeholder + ". TString:  "
						+ tString);
			}

			String style = null;
			if (builders.size() > 2) {
				style = formatTemplate(builders.get(2).toString(), tString,
						locale);
			}

			result.append(handler.handle(locale, parameter.get(), style));
		}
	}

	public void initialize(Map<String, FormatHandler> handlers) {
		this.handlers = new HashMap<>(handlers);
	}

	public static String unEscape(String escaped) {
		StringBuilder sb = new StringBuilder();

		OfInt it = escaped.codePoints().iterator();
		while (it.hasNext()) {
			int c = it.nextInt();
			if (c == '$') {
				if (it.hasNext()) {
					sb.appendCodePoint(it.nextInt());
				}
			} else {
				sb.appendCodePoint(c);
			}
		}
		return sb.toString();
	}

	public static FormatHandler createNumberHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style) {
				NumberFormat format;
				if (style == null || "".equals(style.trim())) {
					format = NumberFormat.getInstance(locale);
				} else {
					switch (style.trim()) {
					case "integer":
						format = NumberFormat.getIntegerInstance(locale);
						break;
					case "currency":
						format = NumberFormat.getCurrencyInstance(locale);
						break;
					case "percent":
						format = NumberFormat.getPercentInstance(locale);
						break;
					default:
						format = new DecimalFormat(style,
								DecimalFormatSymbols.getInstance(locale));
					}
				}
				return format.format(p);
			}
		};
	}

	public static FormatHandler createDateHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style) {
				if (p instanceof Date) {
					DateFormat format;
					Integer formatStyle = parseDateFormat(style);
					if (formatStyle != null) {
						format = DateFormat
								.getDateInstance(formatStyle, locale);
					} else {
						format = new SimpleDateFormat(style, locale);

					}
					return format.format(p);
				}

				if (p instanceof TemporalAccessor) {
					DateTimeFormatter format;

					if (style == null || "".equals(style.trim())) {
						format = DateTimeFormatter.ISO_DATE;
					} else {
						FormatStyle formatStyle = parseFormatStyle(style);
						if (formatStyle != null) {
							format = DateTimeFormatter.ofLocalizedDate(
									formatStyle).withLocale(locale);
						} else {
							format = DateTimeFormatter.ofPattern(style, locale);
						}
					}

					return format.format((TemporalAccessor) p);
				}
				throw new RuntimeException("Unsupported date object: " + p);
			}
		};
	}

	public static FormatHandler createTimeHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style) {
				if (p instanceof Date) {
					DateFormat format;
					Integer formatStyle = parseDateFormat(style);
					if (formatStyle != null) {
						format = DateFormat
								.getTimeInstance(formatStyle, locale);
					} else {
						format = new SimpleDateFormat(style, locale);

					}
					return format.format(p);
				}
				if (p instanceof TemporalAccessor) {
					DateTimeFormatter format;

					if (style == null || "".equals(style.trim())) {
						format = DateTimeFormatter.ISO_TIME;
					} else {
						FormatStyle formatStyle = parseFormatStyle(style);
						if (formatStyle != null) {
							format = DateTimeFormatter.ofLocalizedTime(
									formatStyle).withLocale(locale);
						} else {
							format = DateTimeFormatter.ofPattern(style, locale);
						}
					}

					return format.format((TemporalAccessor) p);
				}
				throw new RuntimeException("Unsupported date object: " + p);
			}

		};
	}

	private static Integer parseDateFormat(String style) {
		if (style == null || "".equals(style.trim())) {
			return DateFormat.DEFAULT;
		} else {
			switch (style.trim()) {

			case "short":
				return DateFormat.SHORT;
			case "medium":
				return DateFormat.MEDIUM;
			case "long":
				return DateFormat.LONG;
			case "full":
				return DateFormat.FULL;
			default:
				return null;
			}
		}
	}

	private static FormatStyle parseFormatStyle(String style) {

		switch (style.trim()) {

		case "short":
			return FormatStyle.SHORT;
		case "medium":
			return FormatStyle.MEDIUM;
		case "long":
			return FormatStyle.LONG;
		case "full":
			return FormatStyle.FULL;
		default:
			return null;
		}
	}

	public static FormatHandler createDateTimeHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style) {
				String dateStyle;
				String timeStyle;
				{
					String[] parts = style.split(",");
					if (parts.length == 1) {
						dateStyle = timeStyle = parts[0];
					} else {
						dateStyle = parts[0];
						timeStyle = parts[1];
					}
				}

				if (p instanceof Date) {
					DateFormat format;
					Integer dateFormatStyle = parseDateFormat(dateStyle);
					Integer timeFormatStyle = parseDateFormat(timeStyle);
					if (dateFormatStyle != null && timeFormatStyle != null) {
						format = DateFormat.getDateTimeInstance(
								dateFormatStyle, timeFormatStyle, locale);
					} else {
						format = new SimpleDateFormat(style, locale);
					}
					return format.format(p);
				}
				if (p instanceof TemporalAccessor) {
					DateTimeFormatter format;

					if ("".equals(dateStyle.trim())
							|| "".equals(timeStyle.trim())) {
						format = DateTimeFormatter.ISO_DATE_TIME;
					} else {
						FormatStyle dateFormatStyle = parseFormatStyle(dateStyle);
						FormatStyle timeFormatStyle = parseFormatStyle(timeStyle);
						if (dateFormatStyle != null && timeFormatStyle != null) {
							format = DateTimeFormatter.ofLocalizedDateTime(
									dateFormatStyle, timeFormatStyle)
									.withLocale(locale);
						} else {
							format = DateTimeFormatter.ofPattern(style, locale);
						}
					}

					return format.format((TemporalAccessor) p);
				}
				throw new RuntimeException("Unsupported date object: " + p);
			}

		};
	}

	public static FormatHandler createChoiceHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style) {
				if (style == null) {
					throw new RuntimeException(
							"Style missing for choice format");
				}
				return new ChoiceFormat(style).format(p);
			}
		};
	}
}
