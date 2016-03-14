package com.github.ruediste.rise.core.strategy;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Optional;
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
        Optional<String> get();
    }

    @Singleton
    static class TestStrategyImpl implements TestStrategy {

        @Override
        public Optional<String> get() {
            return Optional.of("foo");
        }
    }

    @Singleton
    static class SubTestStrategyImpl extends TestStrategyImpl {
        static int invocationCount = 0;

        @Override
        public Optional<String> get() {
            invocationCount++;
            return Optional.empty();
        }
    }

    @Before
    public void before() {
        SubTestStrategyImpl.invocationCount = 0;
        Salta.createInjector().injectMembers(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetStrategyClasses() throws Exception {
        assertThat(strategies.getStrategyClasses(TestStrategyImpl.class), containsInAnyOrder(TestStrategy.class));
        assertThat(strategies.getStrategyClasses(SubTestStrategyImpl.class),
                containsInAnyOrder(TestStrategy.class, TestStrategyImpl.class));
    }

    @Test
    public void testGetStrategies() {
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);
        assertThat(strategies.getStrategies(TestStrategy.class).collect(Collectors.toList()),
                contains(strategy, strategyImpl));
        SubTestStrategyImpl subStrategy = new SubTestStrategyImpl();
        strategies.putStrategyFirst(subStrategy);
        assertThat(strategies.getStrategies(TestStrategy.class).collect(Collectors.toList()),
                contains(subStrategy, strategy, strategyImpl));
    }

    enum TestEnum {
        @UseStrategy(SubTestStrategyImpl.class) VALUE1, VALUE2
    }

    @Test
    public void testGetCachedStrategy() {
        strategies.putStrategy(subStrategy);
        assertEquals(Optional.of("foo"),
                strategies.getStrategy(TestStrategy.class).cached(null).get(TestStrategy::get));
        assertEquals(1, SubTestStrategyImpl.invocationCount);
        assertEquals(Optional.of("foo"),
                strategies.getStrategy(TestStrategy.class).cached(null).get(TestStrategy::get));
        assertEquals(1, SubTestStrategyImpl.invocationCount);
        assertEquals(Optional.of("foo"), strategies.getStrategy(TestStrategy.class).get(TestStrategy::get));
        assertEquals(2, SubTestStrategyImpl.invocationCount);
    }

    @Test
    public void testGetStrategyWithAnnotatedElement() throws Exception {
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);

        assertThat(strategies.getStrategies(TestStrategy.class, TestEnum.class.getField(TestEnum.VALUE2.name()))
                .collect(Collectors.toList()), contains(strategy, strategyImpl));

        assertThat(strategies.getStrategies(TestStrategy.class, TestEnum.class.getField(TestEnum.VALUE1.name()))
                .collect(Collectors.toList()), contains(subStrategy, strategy, strategyImpl));
    }

}
