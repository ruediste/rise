package laf.core.translation;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;

public class ResouceBundleTStringResolverTest {

	ResouceBundleTStringResolver resolver;
	private Locale german;
	private Locale swiss;

	@Before
	public void setup() {
		resolver = new ResouceBundleTStringResolver();
		resolver.initialize("i18n.testResources", getClass().getClassLoader());
		german = Locale.GERMAN;
		swiss = new Locale("de", "CH");
	}

	@Test
	public void testResolve() throws Exception {
		TString str = new TString("test.deOnly");
		assertEquals("german", resolver.resolve(str, german));
		assertEquals("german", resolver.resolve(str, swiss));
		str = new TString("test.deWithCh");
		assertEquals("german", resolver.resolve(str, german));
		assertEquals("swiss", resolver.resolve(str, swiss));
	}

	@Test(expected = MissingResourceException.class)
	public void testResolveNoFallbackFails() throws Exception {
		TString str = new TString("test.notExisting");
		resolver.resolve(str, german);
	}

	@Test
	public void testResolveFallback() throws Exception {
		TString str = new TString("test.notExisting", "fallback");
		assertEquals("fallback", resolver.resolve(str, german));
	}

}
