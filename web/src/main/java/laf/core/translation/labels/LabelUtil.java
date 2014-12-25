package laf.core.translation.labels;

import laf.core.translation.TString;
import laf.core.translation.labels.PropertyReflectionUtil.Property;

import com.google.common.base.CaseFormat;
import com.google.inject.Inject;

public class LabelUtil {

	@Inject
	PropertyReflectionUtil reflectionUtil;

	public TString getLabel(Class<?> type, String name) {
		// find property
		Property property = reflectionUtil.getPropertyIntroduction(type, name);
		if (property == null) {
			throw new IllegalArgumentException("No property " + name
					+ " found in " + type);
		}

		// determine fallback
		String fallback;
		{
			StringBuilder sb = new StringBuilder(name.length());
			boolean isFirst = true;
			for (int c : CaseFormat.LOWER_CAMEL
					.to(CaseFormat.UPPER_CAMEL, name).codePoints().toArray()) {
				if (!isFirst && Character.isUpperCase(c)) {
					sb.append(" ");
				}
				isFirst = false;
				sb.appendCodePoint(c);
			}
			fallback = sb.toString();
		}

		// build and return TString
		return new TString(property.getDeclaringType().getName() + "." + name,
				fallback);
	}
}
