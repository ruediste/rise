package laf.httpRequestMapping.defaultRule;

import java.util.Iterator;

import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoMap;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ControllerInfoRepositoryInitializer;
import laf.controllerInfo.ParameterInfo;
import laf.httpRequest.HttpRequest;
import laf.httpRequest.HttpRequestImpl;
import laf.httpRequestMapping.parameterHandler.ParameterHandler;
import laf.httpRequestMapping.twoStageMappingRule.HttpRequestMapper;
import laf.initialization.LafInitializer;
import laf.initialization.laf.LafInitializationPhase;

import org.slf4j.Logger;

public class DefaultHttpRequestMapper implements HttpRequestMapper {

	public static class Builder {
		@Inject
		ControllerInfoMap.Builder mapBuilder;

		@Inject
		Logger log;

		@Inject
		ControllerInfoRepository controllerInfoRepository;

		public DefaultHttpRequestMapper create(
				ControllerIdentifierStrategy controllerIdentifierStrategy) {
			return new DefaultHttpRequestMapper(controllerInfoRepository,
					mapBuilder.create(controllerIdentifierStrategy), log);
		}
	}

	@LafInitializer(phase = LafInitializationPhase.class, after = ControllerInfoRepositoryInitializer.class)
	public void initialize() {
	}

	private final ControllerInfoMap<String> identifiers;
	private Logger log;
	private ControllerInfoRepository controllerInfoRepository;

	DefaultHttpRequestMapper(ControllerInfoRepository controllerInfoRepository,
			ControllerInfoMap<String> identifiers, Logger log) {
		this.controllerInfoRepository = controllerInfoRepository;
		this.identifiers = identifiers;
		this.log = log;
	}

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
				.substring(identifiers.getMap().get(controllerInfo).length())
				.split("/");

		if (!parts[0].startsWith(".")) {
			log.debug("unable to parse servlet path " + request);
			return null;
		}

		String[] actionNames;
		actionNames = parts[0].substring(1).split("\\.");

		int i = 1;
		for (String actionName : actionNames) {
			ActionInvocation<String> invocation = new ActionInvocation<String>();
			ActionMethodInfo actionMethodInfo = controllerInfo
					.getActionMethodInfo(actionName);
			if (actionMethodInfo == null) {
				log.debug("no ActionMethod named " + actionName + " found");
				return null;
			}

			invocation.setMethodInfo(actionMethodInfo);

			for (; i < parts.length; i++) {
				invocation.getArguments().add(parts[i]);
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
		StringBuilder sb = new StringBuilder();
		// add indentifier
		{
			Iterator<ActionInvocation<String>> it = path.getElements()
					.iterator();
			if (!it.hasNext()) {
				throw new RuntimeException(
						"Tried to generate URL of empty ActionPath");
			}

			ActionInvocation<String> element = it.next();
			ControllerInfo controllerInfo = element.getMethodInfo()
					.getControllerInfo();
			String identifier = identifiers.getMap().get(controllerInfo);
			sb.append(identifier);
		}

		// add methods
		for (ActionInvocation<String> element : path.getElements()) {
			sb.append(".");
			sb.append(element.getMethodInfo().getName());
		}

		// add arguments
		for (ActionInvocation<String> element : path.getElements()) {

			Iterator<ParameterInfo> infoIt = element.getMethodInfo()
					.getParameters().iterator();
			Iterator<String> argIt = element.getArguments().iterator();
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

		return identifiers.getMap().inverse().get(identifier);
	}

	@Override
	public boolean handles(ActionPath<Object> path) {
		return true;
	}

}
