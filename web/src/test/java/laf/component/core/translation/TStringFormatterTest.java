package laf.component.core.translation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TStringFormatterTest {

	@Mock
	ResourceResolver resourceResourver;
	@InjectMocks
	TStringFormatter format;

	@Before
	public void before() {
	}

	@Test
	public void testSimple() {
		when(resourceResourver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"foo");
		assertEquals("foo", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleEscape() {
		when(resourceResourver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"fo#o");
		assertEquals("foo", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameter() {
		when(resourceResourver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {param}");
		assertEquals("the 4", format.format(new TString("resKey", "param", 4),
				Locale.ENGLISH));
	}

	@Test
	public void testSimpleParameterEscape() {
		when(resourceResourver.resolve("resKey", Locale.ENGLISH)).thenReturn(
				"the {pa#}ram}");
		assertEquals("the 4", format.format(new TString("resKey", "pa}ram", 4),
				Locale.ENGLISH));
	}
}
