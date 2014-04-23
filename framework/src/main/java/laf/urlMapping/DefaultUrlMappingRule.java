package laf.urlMapping;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import laf.actionPath.ActionInvocation;
import laf.actionPath.ActionPath;
import laf.attachedProperties.AttachedProperty;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ControllerInfoRepositoryInitializer;
import laf.controllerInfo.ParameterInfo;
import laf.initialization.LafInitializer;
import laf.urlMapping.parameterHandler.ParameterHandler;
import laf.urlMapping.parameterHandler.ParameterHandlerInitializer;
import laf.urlMapping.parameterHandler.ParameterValueProvider;

import org.slf4j.Logger;

import com.google.common.collect.MapMaker;

/**
 * <p>
 * Map URLs in the form
 * &lt;controllerIdentifier>.&lt;method>.&lt;method>/&lt;arg1>/&lt;arg2>
 * </p>
 *
 * <p>
 * For each controller class, a controller identifier is determined, using a
 * {@link ControllerIdentifierStrategy}. The controller identifiers have to be
 * unique and may not contain a dot (.)
 * </p>
 */
public class DefaultUrlMappingRule implements UrlMappingRule {

	@Inject
	Logger log;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	private ControllerIdentifierStrategy controllerIdentifierStrategy = new DefaultControllerIdentifierStrategy();

	private final Map<String, ControllerInfo> controllersByIdentifier = new MapMaker()
			.weakValues().makeMap();

	private static final AttachedProperty<String> controllerIdentifier = new AttachedProperty<>();

	@LafInitializer(after = { ControllerInfoRepositoryInitializer.class,
			ParameterHandlerInitializer.class })
	public void initialize() {
		for (ControllerInfo info : controllerInfoRepository
				.getControllerInfos()) {
			// fill the identifiers map
			String identifier = controllerIdentifierStrategy
					.generateIdentifier(info);
			log.debug("found controller " + identifier);
			controllerIdentifier.set(info, identifier);
			controllersByIdentifier.put(identifier, info);
		}
	}

	@Override
	public ActionPath<ParameterValueProvider> parse(String servletPath) {
		ActionPath<ParameterValueProvider> call = new ActionPath<>();
		ControllerInfo controllerInfo = findControllerEntry(servletPath);

		if (controllerInfo == null) {
			return null;
		}

		// remove the identifier and split the suffix into parts at the /
		// characters
		String[] parts = servletPath.substring(
				controllerIdentifier.get(controllerInfo).length()).split("/");

		if (!parts[0].startsWith(".")) {
			log.debug("unable to parse servlet path " + servletPath);
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
	public String generate(ActionPath<Object> path) {
		StringBuilder sb = new StringBuilder();

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
		return sb.toString();
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

	public ControllerIdentifierStrategy getControllerIdentifierStrategy() {
		return controllerIdentifierStrategy;
	}

	public void setControllerIdentifierStrategy(
			ControllerIdentifierStrategy controllerIdentifierStrategy) {
		this.controllerIdentifierStrategy = controllerIdentifierStrategy;
	}

}