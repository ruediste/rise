package laf.requestProcessing.defaultProcessor;

import laf.actionPath.ActionPath;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.ParameterLoader;

import com.google.common.base.Suppliers;

public class DefaultParameterLoader implements ParameterLoader {

	@Override
	public ActionPath<Object> load(ActionPath<ParameterValueProvider> path) {
		return path.map(Suppliers.supplierFunction());
	}

}