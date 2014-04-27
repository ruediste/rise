package laf.urlMapping;

import static laf.MockitoExt.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;
import laf.LAF;
import laf.LAF.ProjectStage;
import laf.actionPath.ActionPath;
import laf.actionPath.ActionPath.ParameterValueComparator;
import laf.urlMapping.parameterValueProvider.ParameterValueProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.reflect.TypeToken;

public class UrlMappingModuleTest {

	UrlMappingModule mappingModule;
	UrlMappingService mappingService;
	LAF config;
	UrlMappingRule rule;
	ActionPath<ParameterValueProvider> providerPath;
	ActionPath<Object> objectPath;

	@Before
	public void setup() {
		mappingModule = new UrlMappingModule();
		mappingService = new UrlMappingService();
		mappingService.urlMappingModule = mappingModule;
		config = new LAF();
		rule = Mockito.mock(UrlMappingRule.class);
		providerPath = mock(new TypeToken<ActionPath<ParameterValueProvider>>() {
		});

		objectPath = mock(new TypeToken<ActionPath<Object>>() {
		});

		mappingService.laf = config;
		mappingModule.urlMappingRules.getValue().clear();
		mappingModule.urlMappingRules.getValue().add(rule);

		when(rule.parse("foo")).thenReturn(providerPath);
		when(rule.generate(objectPath)).thenReturn("foo");
		when(
				objectPath.isCallToSameActionMethod(
						same(providerPath),
						Matchers.<ParameterValueComparator<Object, ParameterValueProvider>> any()))
				.thenReturn(true);
	}

	@Test
	public void successfulParse() {
		assertEquals(providerPath, mappingService.parse("foo"));
	}

	@Test
	public void parseNoRuleMatches() {
		assertEquals(null, mappingService.parse("foo1"));
	}

	@Test
	public void successfulGenerate() {
		assertEquals("foo", mappingService.generate(objectPath));
	}

	@Test(expected = RuntimeException.class)
	public void generateWrongParsing() {
		when(rule.parse("foo")).thenReturn(
				new ActionPath<ParameterValueProvider>());

		mappingService.generate(objectPath);
	}

	@Test(expected = RuntimeException.class)
	public void generateWrongParsingInProduction() {
		when(rule.parse("foo")).thenReturn(
				new ActionPath<ParameterValueProvider>());
		when(config.getProjectStage()).thenReturn(ProjectStage.PRODUCTION);

		assertEquals("foo", mappingService.generate(objectPath));
	}

}
