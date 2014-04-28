package laf.httpRequestMapping.defaultRule;

import java.util.Iterator;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.controllerInfo.*;
import laf.httpRequest.HttpRequest;
import laf.httpRequest.HttpRequestImpl;
import laf.httpRequestMapping.parameterHandler.ParameterHandler;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.httpRequestMapping.twoStageMappingRule.HttpRequestMapper;

public class DefaultHttpRequestMapper implements HttpRequestMapper {

	@Override
	public ActionPath<String> parse(HttpRequest request) {
		ActionPath<String> call = new ActionPath<>();
		ControllerInfo controllerInfo = findControllerEntry(request.getPath());

		if (controllerInfo == null) {
			return null;
		}

		// remove the identifier and split the suffix into parts at the /
		// characters
		String[] parts = request.getPath()
				.substring(controllerIdentifier.get(controllerInfo).length())
				.split("/");

		if (!parts[0].startsWith(".")) {
			log.debug("unable to parse servlet path " + request);
			return null;
		}

		String[] actionNames;
		actionNames = parts[0].substring(1).split("\\.");

		int i = 1;
		for (String actionName : actionNames) {
			ActionInvocation<ParameterValueProvider> invocation = new ActionInvocation<ParameterValueProvider>();
			ActionMethodInfo actionMethodInfo = controllerInfo
					.getActionMethodInfo(actionName);
			if (actionMethodInfo == null) {
				log.debug("no ActionMethod named " + actionName + " found");
				return null;
			}

			invocation.setMethodInfo(actionMethodInfo);

			Iterator<ParameterInfo> it = actionMethodInfo.getParameters()
					.iterator();
			for (; it.hasNext() && i < parts.length; i++) {
				ParameterInfo parameter = it.next();
				invocation.getArguments().add(
						ParameterHandler.parameterHandler.get(parameter).parse(
								parameter, parts[i]));
			}
			call.getElements().add(invocation);

			if (invocation.getMethodInfo().returnsEmbeddedController()) {
				// update the controller entry to the embedded controller
				controllerInfo = controllerInfoRepository
						.getControllerInfo(invocation.getMethodInfo()
								.getMethod().getReturnType());
			}
		}

		return call;
	}

	@Override
	public HttpRequest generate(ActionPath<String> path) {

		// add indentifier
		{
			Iterator<ActionInvocation<Object>> it = path.getElements()
					.iterator();
			if (!it.hasNext()) {
				throw new RuntimeException(
						"Tried to generate URL of empty ActionPath");
			}

			ActionInvocation<Object> element = it.next();
			ControllerInfo controllerInfo = element.getMethodInfo()
					.getControllerInfo();
			String identifier = controllerIdentifier.get(controllerInfo);
			sb.append(identifier);
		}

		// add methods
		for (ActionInvocation<Object> element : path.getElements()) {
			sb.append(".");
			sb.append(element.getMethodInfo().getName());
		}

		// add arguments
		for (ActionInvocation<Object> element : path.getElements()) {

			Iterator<ParameterInfo> infoIt = element.getMethodInfo()
					.getParameters().iterator();
			Iterator<Object> argIt = element.getArguments().iterator();
			while (infoIt.hasNext() && argIt.hasNext()) {
				ParameterInfo info = infoIt.next();
				sb.append("/");
				sb.append(ParameterHandler.parameterHandler.get(info).generate(
						info, argIt.next()));
			}
		}
		return new HttpRequestImpl(sb.toString());
	}

	private ControllerInfo findControllerEntry(String servletPath) {
		// find the first dot in the path, which separates the controller from
		// the method
		int idx = servletPath.indexOf('.');
		if (idx < 0) {
			log.debug("No dot in servlet Path, cannot determine controller");
			return null;
		}

		// get the prefix
		String identifier = servletPath.substring(0, idx);

		return controllersByIdentifier.get(identifier);
	}

	@Override
	public boolean handles(ActionPath<Object> path) {
		return true;
	}

}
