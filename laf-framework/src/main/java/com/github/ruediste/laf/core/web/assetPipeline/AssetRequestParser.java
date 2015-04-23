package com.github.ruediste.laf.core.web.assetPipeline;

import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.RequestParser;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;

public class AssetRequestParser implements RequestParser {

	public final class AssetRequestParseResult implements RequestParseResult {
		@Override
		public void handle() {

		}
	}

	@Override
	public RequestParseResult parse(HttpRequest request) {
		return new AssetRequestParseResult();
	}

}
