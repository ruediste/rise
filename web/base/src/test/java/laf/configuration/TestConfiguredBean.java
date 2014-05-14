package laf.configuration;

import java.util.*;

import javax.inject.Inject;

public class TestConfiguredBean {

	@Inject
	@ConfigValue("foo")
	public String string;

	@Inject
	@ConfigValue("4")
	public int integer;

	@Inject
	@ConfigValue("java.lang.Double")
	public Class<? extends Number> clazz;

	@ConfigValue("laf.configuration.TestBeanA")
	@Inject
	ConfigInstance<TestBeanA> testBeanA;

	@ConfigValue("laf.configuration.TestBeanB1, laf.configuration.TestBeanB2")
	@Inject
	List<ITestBeanB> testBeanBs;

	@ConfigValue("laf.configuration.TestBeanB1, laf.configuration.TestBeanB2")
	@Inject
	ArrayList<ITestBeanB> testBeanBsArrayList;

	@ConfigValue("laf.configuration.TestBeanB1, laf.configuration.TestBeanB2")
	@Inject
	LinkedList<ITestBeanB> testBeanBsLinkedList;

	@ConfigValue("laf.configuration.TestBeanB1, laf.configuration.TestBeanB2")
	@Inject
	Deque<ITestBeanB> testBeanBsDeQueue;

	@Inject
	@ConfigValue("laf.configuration.TestBeanB1")
	ConfigInstance<ITestBeanB> testBeanB1;
}
