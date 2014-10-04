package laf.component.core.translation;

import java.util.Locale;

import com.google.common.base.CaseFormat;

/**
 * Meta annotation for annotations which represent a translation to a certain
 * language.
 *
 * <p>
 * If the {@link #value()} is defined, the annotated annotation represents the
 * translations for the given {@link Locale}. If the {@link #value()} is left
 * empty, the locale is derived from the annotation name. The annotation name is
 * interpreted as {@link CaseFormat#UPPER_CAMEL}. The first word is taken as the
 * language, the second, if present, as country (converted to all upper case).
 * The rest is taken as is as the locale variant.
 * </p>
 *
 * <p>
 * The value attribute of annotated annotations has to be of {@link String}
 * type. It has the following format:
 *
 * <pre>
 * {@code
 * <default variant text> [; <variant id>: <variant text>]}*
 * </pre>
 *
 * The backslash \ is used as escaping character. A character immediately
 * following the backslash loses any special meaning. The backslashes themselves
 * are removed from the string. A double backslash \\ is converted to a single
 * backslash.
 * <p>
 *
 * <p>
 * Any other attributes of {@link String} type are interpreted as translation
 * variants. The variant name is taken from the attribute name
 * </p>
 */
public @interface LanguageAnnotation {
	/**
	 * Locale represented by the annotated annotation
	 */
	String value() default "";
}
