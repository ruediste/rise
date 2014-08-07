package laf.core.requestProcessing;

import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

import com.google.common.base.Suppliers;

public class DefaultParameterLoader implements ParameterLoader {

	@Override
	public ActionPath<Object> load(ActionPath<ParameterValueProvider> path) {
		return path.map(Suppliers.supplierFunction());
	}

}