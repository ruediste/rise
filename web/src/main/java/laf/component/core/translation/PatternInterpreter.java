package laf.component.core.translation;

import java.text.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Function;

/**
 * Interprets patterns to generate {@link String}s, closely following the
 * semantics of {@link MessageFormat}.
 *
 * <p>
 * The interpretation of a pattern always happens in the context of a locale and
 * a parameter map.
 * </p>
 *
 * <p>
 * The main differences from MessageFormat are:
 * <ul>
 * <li>Escaping with dollar signs instead of single colons</li>
 * <li>parameters are identified by string keys instead of numbers</li>
 * </ul>
 * </p>
 *
 * <p>
 * The supported formats are not hard coded. They are defined by the registered
 * {@link FormatHandler}s (see {@link #initialize(Map)})
 * </p>
 */
public class PatternInterpreter {

	/**
	 * Interface for formatting placeholders in patterns
	 */
	public interface FormatHandler {

		public String handle(Locale locale, Object parameter, String style,
				Function<String, String> formatFunc);
	}

	private Map<String, FormatHandler> handlers;

	/**
	 * Generate a {@link String} from a pattern, substituting all parameters.
	 */
	public String interpret(String pattern, Map<String, Object> parameters,
			Locale locale) {
		StringBuilder result = new StringBuilder();

		OfInt it = pattern.codePoints().iterator();
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
											+ pattern);
						}
						if (placeholderDepth == 1) {
							// we are closing the placeholder, resolve it
							resolvePlaceholder(locale, result, parameters,
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
			Map<String, Object> parameters, String placeholder) {
		ArrayList<StringBuilder> placeholderParts = new ArrayList<>(3);
		StringBuilder sb = new StringBuilder();

		OfInt it = placeholder.codePoints().iterator();
		while (it.hasNext()) {
			int c = it.nextInt();
			if (c == '$') {
				// the style keeps the escaping
				if (placeholderParts.size() > 1) {
					sb.appendCodePoint(c);
				}
				if (it.hasNext()) {
					sb.appendCodePoint(it.nextInt());
				}
			} else if (c == ',' && placeholderParts.size() < 2) {
				placeholderParts.add(sb);
				sb = new StringBuilder();
			} else {
				sb.appendCodePoint(c);
			}
		}
		placeholderParts.add(sb);

		if (placeholderParts.size() == 0) {
			throw new RuntimeException("no parameter key found in "
					+ placeholder);
		}

		String parameterKey = placeholderParts.get(0).toString().trim();

		// check if parameter is present
		if (!parameters.containsKey(parameterKey)) {
			throw new RuntimeException("Unknown parameter " + parameterKey
					+ " in string " + parameters);
		}

		Object parameter = parameters.get(parameterKey);
		if (placeholderParts.size() == 1) {
			// there was only the parameter name in the placeholder
			// append using toString()
			result.append(parameter);
		} else {
			// there is the parameter name and a format name in the placeholder

			// retrieve the format handler for the format name
			String format = placeholderParts.get(1).toString().trim();
			FormatHandler handler = handlers.get(format);
			if (handler == null) {
				throw new RuntimeException("Unknow format " + format
						+ " in placeholder " + placeholder + ". TString:  "
						+ parameters);
			}

			// retrieve the style if available (third part of the placeholder)
			String style = null;
			if (placeholderParts.size() == 3) {
				style = placeholderParts.get(2).toString();
			}

			// apply the format to the parameter
			result.append(handler.handle(locale, parameter, style,
					s -> interpret(s, parameters, locale)));
		}
	}

	/**
	 * Initialize this formatter and register the provided {@link FormatHandler}
	 * s
	 */
	public void initialize(Map<String, FormatHandler> handlers) {
		this.handlers = new HashMap<>(handlers);
	}

	/**
	 * Create a map containing the default format handlers
	 */
	public static HashMap<String, FormatHandler> defaultHandlerMap() {
		HashMap<String, FormatHandler> handlers = new HashMap<>();
		handlers.put("date", PatternInterpreter.createDateHandler());
		handlers.put("time", PatternInterpreter.createTimeHandler());
		handlers.put("dateTime", PatternInterpreter.createDateTimeHandler());
		handlers.put("choice", PatternInterpreter.createChoiceHandler());
		handlers.put("number", PatternInterpreter.createNumberHandler());
		return handlers;
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
			public String handle(Locale locale, Object p, String style,
					Function<String, String> formatFunc) {
				NumberFormat format;
				if (style == null || "".equals(unEscape(style).trim())) {
					format = NumberFormat.getInstance(locale);
				} else {
					switch (unEscape(style).trim()) {
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
						format = new DecimalFormat(unEscape(style),
								DecimalFormatSymbols.getInstance(locale));
					}
				}
				return format.format(p);
			}
		};
	}

	private static Integer parseDateFormat(String style) {
		if (style == null || "".equals(unEscape(style).trim())) {
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

		switch (unEscape(style).trim()) {

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

	public static FormatHandler createDateHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style,
					Function<String, String> formatFunc) {
				if (p instanceof Date) {
					DateFormat format;
					Integer formatStyle = parseDateFormat(style);
					if (formatStyle != null) {
						format = DateFormat
								.getDateInstance(formatStyle, locale);
					} else {
						format = new SimpleDateFormat(unEscape(style), locale);

					}
					return format.format(p);
				}

				if (p instanceof TemporalAccessor) {
					DateTimeFormatter format;

					if (style == null || "".equals(unEscape(style).trim())) {
						format = DateTimeFormatter.ISO_DATE;
					} else {
						FormatStyle formatStyle = parseFormatStyle(style);
						if (formatStyle != null) {
							format = DateTimeFormatter.ofLocalizedDate(
									formatStyle).withLocale(locale);
						} else {
							format = DateTimeFormatter.ofPattern(
									unEscape(style), locale);
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
			public String handle(Locale locale, Object p, String style,
					Function<String, String> formatFunc) {
				if (p instanceof Date) {
					DateFormat format;
					Integer formatStyle = parseDateFormat(style);
					if (formatStyle != null) {
						format = DateFormat
								.getTimeInstance(formatStyle, locale);
					} else {
						format = new SimpleDateFormat(unEscape(style), locale);

					}
					return format.format(p);
				}
				if (p instanceof TemporalAccessor) {
					DateTimeFormatter format;

					if (style == null || "".equals(unEscape(style).trim())) {
						format = DateTimeFormatter.ISO_TIME;
					} else {
						FormatStyle formatStyle = parseFormatStyle(style);
						if (formatStyle != null) {
							format = DateTimeFormatter.ofLocalizedTime(
									formatStyle).withLocale(locale);
						} else {
							format = DateTimeFormatter.ofPattern(
									unEscape(style), locale);
						}
					}

					return format.format((TemporalAccessor) p);
				}
				throw new RuntimeException("Unsupported date object: " + p);
			}

		};
	}

	public static FormatHandler createDateTimeHandler() {
		return new FormatHandler() {
			@Override
			public String handle(Locale locale, Object p, String style,
					Function<String, String> formatFunc) {
				String dateStyle;
				String timeStyle;
				if (style == null) {
					dateStyle = "";
					timeStyle = "";
				} else {
					ArrayList<StringBuilder> builders = new ArrayList<>();
					StringBuilder sb = new StringBuilder();
					OfInt it = style.codePoints().iterator();
					while (it.hasNext()) {
						int c = it.nextInt();
						if (c == '$') {
							if (it.hasNext()) {
								sb.appendCodePoint(it.nextInt());
							}
						} else if (c == ',') {
							builders.add(sb);
							sb = new StringBuilder();
						} else {
							sb.appendCodePoint(c);
						}
					}
					builders.add(sb);

					dateStyle = builders.get(0).toString();
					if (builders.size() == 1) {
						timeStyle = dateStyle;
					} else if (builders.size() == 2) {
						timeStyle = builders.get(1).toString();
					} else {
						throw new RuntimeException(
								"Too many commas in date time style. Escape commas in patterns");
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
						format = new SimpleDateFormat(unEscape(style), locale);
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
							format = DateTimeFormatter.ofPattern(
									unEscape(style), locale);
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
			public String handle(Locale locale, Object p, String style,
					Function<String, String> formatFunc) {

				// check for empty styles
				if (style == null || "".equals(unEscape(style).trim())) {
					throw new RuntimeException(
							"Style missing for choice format");
				}

				// Format the style. Allows for parameter substitution within
				// the template
				String choicePattern = formatFunc.apply(style);

				// format using the ChoiceFormat
				return new ChoiceFormat(choicePattern).format(p);
			}
		};
	}
}
