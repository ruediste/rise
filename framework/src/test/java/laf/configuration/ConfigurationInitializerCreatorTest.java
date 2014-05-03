package laf.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import laf.configuration.ConfigurationInitializerCreator.ConfigurationInitializer;
import laf.initialization.*;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Iterables;

public class ConfigurationInitializerCreatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testConfigurationInitializer() {
		ConfigurationInitializerCreator creator = new ConfigurationInitializerCreator();
		ConfigurationInitializer initializer = creator.new ConfigurationInitializer();
		Initializer i = mock(Initializer.class);
		Mockito.when(i.getRepresentingClass()).thenReturn((Class) Float.class);
		initializer.addInitializer(i);
		assertTrue(initializer.getRelatedRepresentingClasses().contains(
				Float.class));

		// check relation to initializer added above
		Collection<InitializerDependsRelation> declaredRelations = initializer
				.getDeclaredRelations(i);
		assertFalse(declaredRelations.isEmpty());
		assertEquals(new InitializerDependsRelation(initializer, i, false),
				Iterables.getOnlyElement(declaredRelations));

		// check relation to FrameworkRootInitializer
		assertTrue(initializer.getRelatedRepresentingClasses().contains(
				FrameworkRootInitializer.class));
		Mockito.when(i.getRepresentingClass()).thenReturn(
				(Class) FrameworkRootInitializer.class);
		assertEquals(new InitializerDependsRelation(i, initializer, false),
				Iterables.getOnlyElement(initializer.getDeclaredRelations(i)));

		// check relation to arbitrary initializer
		i = mock(Initializer.class);
		Mockito.when(i.getRepresentingClass()).thenReturn((Class) Double.class);
		assertTrue(initializer.getDeclaredRelations(i).isEmpty());

	}
}
