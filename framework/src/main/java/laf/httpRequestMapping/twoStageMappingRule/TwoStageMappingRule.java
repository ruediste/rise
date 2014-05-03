package laf.httpRequestMapping.twoStageMappingRule;

import java.util.Arrays;

import laf.actionPath.ActionPath;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.initialization.InitializerProvider;
import laf.initialization.Phase;

/**
 * {@link HttpRequestMappingRule} using a {@link HttpRequestMapper} and a
 * {@link ParameterMapper}.
 *
 * <p>
 * <img src="doc-files/TwoStageMappingRule.png" />
 * </p>
 */
public class TwoStageMappingRule implements HttpRequestMappingRule,
InitializerProvider {

	private final HttpRequestMapper httpRequestMapper;
	private final ParameterMapper parameterMapper;
	private final ActionPathSigner signer;

	public TwoStageMappingRule(HttpRequestMapper httpRequestMapper,
			ParameterMapper parameterMapper, ActionPathSigner signer) {
		this.httpRequestMapper = httpRequestMapper;
		this.parameterMapper = parameterMapper;
		this.signer = signer;
	}

	@Override
	public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
		ActionPath<String> stringPath = httpRequestMapper.parse(request);

		if (stringPath == null) {
			return null;
		}

		if (!signer.isSignatureValid(stringPath)) {
			throw new RuntimeException("Signature of " + stringPath
					+ " is not valid");
		}

		return parameterMapper.parse(stringPath);
	}

	@Override
	public HttpRequest generate(ActionPath<Object> path) {
		if (!httpRequestMapper.handles(path)) {
			return null;
		}
		ActionPath<String> stringPath = parameterMapper.generate(path);

		signer.sign(stringPath);

		return httpRequestMapper.generate(stringPath);
	}

	@Override
	public Iterable<Object> getInitializers(Class<? extends Phase> phase) {
		return Arrays.asList(httpRequestMapper, parameterMapper, signer);
	}

}
