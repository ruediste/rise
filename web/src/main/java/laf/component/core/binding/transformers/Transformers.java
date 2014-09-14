package laf.component.core.binding.transformers;

import java.util.Date;

public class Transformers {

	public static String dateToString(Date date) {
		return new DateToStringTransformer().transform(date);
	}

	public static Date stringToDate(String string) {
		return new DateToStringTransformer().transformInv(string);
	}
}
