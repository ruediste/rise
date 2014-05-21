package laf.httpRequestParsing.twoStageMappingRule;

import laf.actionPath.ActionPath;
import laf.httpRequest.HttpRequest;
import laf.httpRequestParsing.HttpRequestParsingRule;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

/**
 * {@link HttpRequestParsingRule} using a {@link HttpRequestMapper} and a
 * {@link ParameterMapper}.
 *
 * <p>
 * <img src="doc-files/TwoStageMappingRule.png" />
 * </p>
 */
public class TwoStageMappingRule implements HttpRequestParsingRule {

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

}
