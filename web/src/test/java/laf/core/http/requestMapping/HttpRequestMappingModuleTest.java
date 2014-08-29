package laf.core.http.requestMapping;

import static laf.test.MockitoExt.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import laf.core.base.BaseModule;
import laf.core.base.configuration.ConfigurationValueImpl;
import laf.core.http.request.HttpRequestImpl;
import laf.core.http.requestMapping.*;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.mvc.core.actionPath.ActionPath;
import laf.mvc.core.actionPath.ActionPath.ParameterValueComparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.reflect.TypeToken;

public class HttpRequestMappingModuleTest {

	HttpRequestMappingModule mappingModule;
	HttpRequestMappingService mappingService;
	BaseModule baseModule;
	HttpRequestMappingRule rule;
	ActionPath<ParameterValueProvider> providerPath;
	ActionPath<Object> objectPath;

	@Before
	public void setup() {
		mappingModule = new HttpRequestMappingModule();
		mappingService = new HttpRequestMappingService();
		baseModule = mock(BaseModule.class);
		rule = Mockito.mock(HttpRequestMappingRule.class);
		providerPath = mock(new TypeToken<ActionPath<ParameterValueProvider>>() {
		});

		objectPath = mock(new TypeToken<ActionPath<Object>>() {
		});

		mappingService.baseModule = baseModule;
		mappingService.mappingRules = new ConfigurationValueImpl<HttpRequestMappingRules>(
				new HttpRequestMappingRules() {

					@Override
					public void set(Deque<HttpRequestMappingRule> value) {
					}

					@Override
					public Deque<HttpRequestMappingRule> get() {
						return new ArrayDeque<>(Collections.singletonList(rule));
					}
				});

		when(rule.parse(new HttpRequestImpl("foo"))).thenReturn(providerPath);
		when(rule.generate(objectPath)).thenReturn(new HttpRequestImpl("foo"));
		when(
				objectPath.isCallToSameMethod(
						same(providerPath),
						Matchers.<ParameterValueComparator<Object, ParameterValueProvider>> any()))
						.thenReturn(true);
	}

	@Test
	public void successfulParse() {
		assertEquals(providerPath,
				mappingService.parse(new HttpRequestImpl("foo")));
	}

	@Test
	public void parseNoRuleMatches() {
		assertEquals(null, mappingService.parse(new HttpRequestImpl("foo1")));
	}

	@Test
	public void successfulGenerate() {
		assertEquals(new HttpRequestImpl("foo"),
				mappingService.generate(objectPath));
	}

	@Test(expected = RuntimeException.class)
	public void generateWrongParsing() {
		when(rule.parse(new HttpRequestImpl("foo"))).thenReturn(
				new ActionPath<ParameterValueProvider>());

		mappingService.generate(objectPath);
	}

	@Test
	public void generateWrongParsingInProduction() {
		when(rule.parse(new HttpRequestImpl("foo"))).thenReturn(
				new ActionPath<ParameterValueProvider>());
		when(baseModule.getProjectStage()).thenReturn(
				laf.core.base.BaseModule.ProjectStage.PRODUCTION);

		mappingService.generate(objectPath);

		assertEquals(new HttpRequestImpl("foo"),
				mappingService.generate(objectPath));
	}

}
