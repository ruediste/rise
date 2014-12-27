package laf.core.translation.message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import laf.core.base.StringUtil;
import laf.core.translation.PString;
import laf.core.translation.TString;

import com.google.common.base.CaseFormat;

@TMessages
public class TMessageInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		String fallback;
		TMessage tMessage = method.getAnnotation(TMessage.class);
		if (tMessage != null) {
			fallback = tMessage.value();
		} else {
			fallback = StringUtil
					.insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
							CaseFormat.UPPER_CAMEL, method.getName()))
					+ ".";
		}

		HashMap<String, Object> parameters = new HashMap<>();
		TString tString = new TString(method.getDeclaringClass().getName()
				+ "." + method.getName(), fallback);
		for (int i = 0; i < method.getParameters().length; i++) {
			parameters.put(method.getParameters()[i].getName(), args[i]);
		}

		return new PString(tString, parameters);
	}

}
