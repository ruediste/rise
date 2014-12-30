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

		// calculate fallback
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

		// build string
		TString tString = new TString(method.getDeclaringClass().getName()
				+ "." + method.getName(), fallback);

		// check return type
		if (TString.class.equals(method.getReturnType())) {
			if (args != null && args.length > 0) {
				throw new RuntimeException(
						"The return type of "
								+ method
								+ " is TString but there are parameters. Change the return type to PString instead");
			}
			return tString;
		}

		if (PString.class.equals(method.getReturnType())) {
			// build parameter map
			HashMap<String, Object> parameters = new HashMap<>();
			for (int i = 0; i < method.getParameters().length; i++) {
				parameters.put(method.getParameters()[i].getName(), args[i]);
			}

			return new PString(tString, parameters);
		}

		throw new RuntimeException("The return type of " + method
				+ " must be TString or PString");
	}

}
