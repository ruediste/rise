package com.github.ruediste.rise.core.web.assetPipeline;

import com.github.ruediste.rise.core.RequestParseResult;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;

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
