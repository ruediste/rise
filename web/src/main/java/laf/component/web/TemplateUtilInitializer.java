package laf.component.web;

import java.util.Deque;

import javax.inject.Inject;

public class TemplateUtilInitializer implements Runnable {

	@Inject
	TemplateUtil util;
	private Deque<HtmlTemplateFactory> factories;

	public void initialize(Deque<HtmlTemplateFactory> factories) {
		this.factories = factories;

	}

	@Override
	public void run() {
		util.initialize(factories);
	}
}
