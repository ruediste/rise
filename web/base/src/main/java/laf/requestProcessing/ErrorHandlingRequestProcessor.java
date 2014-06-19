package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

/**
 * {@link RequestProcessor} redirecting to the error destination when an
 * exception is thrown by the delegate.
 */
public class ErrorHandlingRequestProcessor extends DelegatingRequestProcessor {

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		try {
			return getDelegate().process(path);
		} catch (Exception e) {
			// TODO: implement error redirection
			throw e;
		}
	}

}
