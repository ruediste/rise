package com.github.ruediste.rise.core.strategy;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class StrategiesTest {

    @Inject
    Strategies strategies;

    @Inject
    TestStrategyImpl strategyImpl;

    @Inject
    SubTestStrategyImpl subStrategy;

    @Inject
    Injector injector;

    @ImplementedBy(TestStrategyImpl.class)
    interface TestStrategy extends Strategy {

    }

    @Singleton
    static class TestStrategyImpl implements TestStrategy {

    }

    @Singleton
    static class SubTestStrategyImpl extends TestStrategyImpl {

    }

    @SuppressWarnings({ "unchecked" })
    @Before
    public void before() {
        Salta.createInjector().injectMembers(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetStrategyClasses() throws Exception {
        assertThat(strategies.getStrategyClasses(TestStrategyImpl.class),
                containsInAnyOrder(TestStrategy.class));
        assertThat(strategies.getStrategyClasses(SubTestStrategyImpl.class),
                containsInAnyOrder(TestStrategy.class, TestStrategyImpl.class));
    }

    @Test
    public void testGetStrategies() {
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);
        assertThat(strategies.getStrategies(TestStrategy.class).collect(
                Collectors.toList()), contains(strategy, strategyImpl));
        SubTestStrategyImpl subStrategy = new SubTestStrategyImpl();
        strategies.putStrategyFirst(subStrategy);
        assertThat(
                strategies.getStrategies(TestStrategy.class)
                        .collect(Collectors.toList()),
                contains(subStrategy, strategy, strategyImpl));
    }

    enum TestEnum {
        @UseStrategy(SubTestStrategyImpl.class) VALUE1, VALUE2
    }

    @Test
    public void testGetStrategyWithAnnotatedElement() throws Exception {
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);

        assertThat(
                strategies
                        .getStrategies(TestStrategy.class,
                                TestEnum.class.getField(TestEnum.VALUE2.name()))
                        .collect(Collectors.toList()),
                contains(strategy, strategyImpl));

        assertThat(
                strategies
                        .getStrategies(TestStrategy.class,
                                TestEnum.class.getField(TestEnum.VALUE1.name()))
                        .collect(Collectors.toList()),
                contains(subStrategy, strategy, strategyImpl));
    }

}
