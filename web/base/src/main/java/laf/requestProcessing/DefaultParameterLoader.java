package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

import com.google.common.base.Suppliers;

public class DefaultParameterLoader implements ParameterLoader {

	@Override
	public ActionPath<Object> load(ActionPath<ParameterValueProvider> path) {
		return path.map(Suppliers.supplierFunction());
	}

}