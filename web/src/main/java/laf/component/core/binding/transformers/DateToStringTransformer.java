package laf.component.core.binding.transformers;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import laf.component.core.binding.BindingTransformer;

public class DateToStringTransformer extends BindingTransformer<String, Date> {

	@Override
	public String transformPullUp(Date model) {
		return DateFormat.getDateTimeInstance().format(model);
	}

	@Override
	public Date transformPushDown(String view) {
		try {
			return DateFormat.getDateTimeInstance().parse(view);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
