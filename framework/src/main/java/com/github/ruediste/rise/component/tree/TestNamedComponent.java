package com.github.ruediste.rise.component.tree;

public interface TestNamedComponent<TSelf extends TestNamedComponent<TSelf>>
        extends Component {

    String TEST_NAME();

    TSelf TEST_NAME(String testName);

}
