package com.github.ruediste.rise.component.components;

public interface TestNamedComponent<TSelf extends TestNamedComponent<TSelf>> {

    String TEST_NAME();

    TSelf TEST_NAME(String testName);

}
