package com.github.ruediste.rise.testApp.component;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.github.ruediste.rise.component.ComponentViewRepository;
import com.github.ruediste.rise.testApp.WebTest;

public class SampleComponentViewTest extends WebTest {

	@Inject
	ComponentViewRepository repo;

	@Test
	public void viewForSampleControllerFound() {
		assertEquals(SampleComponentView.class,
				repo.createView(new SampleComponentController()).getClass());
		assertEquals(
				SampleComponentViewAlternative.class,
				repo.createView(new SampleComponentController(),
						SampleViewQualifier.class).getClass());
	}
}
