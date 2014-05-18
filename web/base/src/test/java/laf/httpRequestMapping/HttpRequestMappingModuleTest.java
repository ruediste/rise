package laf.httpRequestMapping;

import static laf.MockitoExt.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.base.BaseModule;
import laf.httpRequest.HttpRequestImpl;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

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
		mappingService.mappingRules.add(rule);

		when(rule.parse(new HttpRequestImpl("foo"))).thenReturn(providerPath);
		when(rule.generate(objectPath)).thenReturn(new HttpRequestImpl("foo"));
		when(
				objectPath.isCallToSameActionMethod(
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
				laf.base.BaseModule.ProjectStage.PRODUCTION);

		mappingService.generate(objectPath);

		assertEquals(new HttpRequestImpl("foo"),
				mappingService.generate(objectPath));
	}

}