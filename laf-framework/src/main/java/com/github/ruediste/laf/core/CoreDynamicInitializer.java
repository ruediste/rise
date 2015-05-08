package com.github.ruediste.laf.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.front.ReloadCountHolder;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.laf.core.web.assetPipeline.AssetRequestMapper;
import com.github.ruediste.laf.util.Initializer;

public class CoreDynamicInitializer implements Initializer {

	@Inject
	CoreConfiguration config;

	@Inject
	AssetPipelineConfiguration pipelineConfig;

	@Inject
	AssetRequestMapper assetRequestMapper;

	@Inject
	PathInfoIndex index;

	@Inject
	ReloadCountHolder holder;

	@Inject
	CoreRequestInfo info;

	@Override
	public void initialize() {
		config.dynamicClassLoader = Thread.currentThread()
				.getContextClassLoader();
		config.initialize();
		pipelineConfig.initialize();
		assetRequestMapper.initialize();

		index.registerPathInfo(config.reloadQueryPathInfo.getValue(),
				new RequestParser() {

					@Override
					public RequestParseResult parse(HttpRequest request) {

						return new RequestParseResult() {

							@Override
							public void handle() {
								boolean doReload = holder.waitForReload(Long
										.parseLong(request.getParameter("nr")));
								HttpServletResponse response = info
										.getServletResponse();

								response.setContentType("text/plain;charset=utf-8");
								PrintWriter out;
								try {
									out = response.getWriter();
									out.write(doReload ? "true" : "false");
									out.close();
								} catch (IOException e) {
									throw new RuntimeException(
											"Error sending response");
								}
							}
						};
					}
				});
	}

}
