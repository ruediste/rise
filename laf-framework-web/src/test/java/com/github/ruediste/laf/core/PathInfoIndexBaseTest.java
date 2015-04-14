package com.github.ruediste.laf.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PathInfoIndexBaseTest {

	PathInfoIndexBase<Runnable> index;

	@Before
	public void setup() {
		index = new PathInfoIndexBase<>();
	}

	@Test
	public void registeredPathInfoFound() {
		Runnable handler = () -> {
		};
		index.registerPathInfo("test", handler);
		assertThat(index.getHandler("test"), is(handler));
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDuplicatePathInfosFails() {
		index.registerPathInfo("test", () -> {
		});
		index.registerPathInfo("test", () -> {
		});
		fail();
	}

	@Test
	public void registeredPrefixFound() {
		Runnable handler = () -> {
		};
		index.registerPrefix("test", handler);
		assertThat(index.getHandler("test"), is(handler));
		assertThat(index.getHandler("testTheFoo"), is(handler));
		assertThat(index.getHandler("tes"), nullValue());
		assertThat(index.getHandler("tesa"), nullValue());
		assertThat(index.getHandler("tesz"), nullValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDuplicatePrefixFails() {
		index.registerPrefix("test", () -> {
		});
		index.registerPrefix("test", () -> {
		});
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerAmbigousPrefixFails1() {
		index.registerPrefix("test", () -> {
		});
		index.registerPrefix("testFoo", () -> {
		});
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerAmbigousPrefixFails2() {
		index.registerPrefix("testFoo", () -> {
		});
		index.registerPrefix("test", () -> {
		});
		fail();
	}

	@Test
	public void registerTwoPrefixes() {
		Runnable handlera = () -> {
		};
		Runnable handlerb = () -> {
		};
		index.registerPrefix("testa", handlera);
		index.registerPrefix("testb", handlerb);
		assertThat(index.getHandler("testab"), is(handlera));
		assertThat(index.getHandler("testbb"), is(handlerb));
	}
}
