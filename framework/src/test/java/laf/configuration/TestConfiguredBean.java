package laf.configuration;

import javax.inject.Inject;

public class TestConfiguredBean {

	@Inject
	@ConfigValue("foo")
	public String string;

	@Inject
	@ConfigValue("4")
	public int integer;

	@Inject
	@ConfigValue("java.lang.Doublee")
	public Class<? extends Number> clazz;

}
