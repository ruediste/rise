package laf.urlMapping;

import static laf.MockitoExt.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import laf.LAF;
import laf.LAF.ProjectStage;
import laf.urlMapping.ActionPath.ParameterValueComparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.google.common.reflect.TypeToken;

public class UrlMappingTest {

	UrlMapping mapping;
	LAF config;
	UrlMappingRule rule;
	ActionPath<ParameterValueProvider> providerPath;
	ActionPath<Object> objectPath;

	@Before
	public void setup() {
		mapping = new UrlMapping();
		config = new LAF();
		rule = mock(UrlMappingRule.class);
		providerPath = mock(new TypeToken<ActionPath<ParameterValueProvider>>() {
		});

		objectPath = mock(new TypeToken<ActionPath<Object>>() {
		});

		mapping.coreConfig = config;
		config.getUrlMappingRules().add(rule);

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
		assertEquals(providerPath, mapping.parse("foo"));
	}

	@Test
	public void parseNoRuleMatches() {
		assertEquals(null, mapping.parse("foo1"));
	}

	@Test
	public void successfulGenerate() {
		assertEquals("foo", mapping.generate(objectPath));
	}

	@Test(expected = RuntimeException.class)
	public void generateWrongParsing() {
		when(rule.parse("foo")).thenReturn(
				new ActionPath<ParameterValueProvider>());

		mapping.generate(objectPath);
	}

	@Test(expected = RuntimeException.class)
	public void generateWrongParsingInProduction() {
		when(rule.parse("foo")).thenReturn(
				new ActionPath<ParameterValueProvider>());
		when(config.getProjectStage()).thenReturn(ProjectStage.PRODUCTION);

		assertEquals("foo", mapping.generate(objectPath));
	}

}
