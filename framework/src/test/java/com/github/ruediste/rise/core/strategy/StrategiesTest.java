package com.github.ruediste.rise.core.strategy;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.salta.jsr330.Injector;

public class StrategiesTest {

    Strategies strategies;
    
    interface TestStrategy extends Strategy<String>{
        
    }
    
    class TestStrategyImpl implements TestStrategy{

        @Override
        public boolean applies(String key) {
            return true;
        }
    }
    class SubTestStrategyImpl extends TestStrategyImpl{
        
    }

    @Before
    public void before(){
        strategies=new  Strategies();
        strategies.injector=mock(Injector.class);
        when(strategies.injector.getInstance(SubTestStrategyImpl.class)).thenReturn(new SubTestStrategyImpl());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetStrategyClasses() throws Exception {
        assertThat(strategies.getStrategyClasses(TestStrategyImpl.class),containsInAnyOrder(TestStrategy.class));
        assertThat(strategies.getStrategyClasses(SubTestStrategyImpl.class),containsInAnyOrder(TestStrategy.class,TestStrategyImpl.class));
    }
    
    @Test
    public void testGetStrategy(){
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);
        assertEquals(Optional.of(strategy),strategies.getStrategy(TestStrategy.class, ""));
        SubTestStrategyImpl subStrategy=new SubTestStrategyImpl();
        strategies.putStrategyFirst(subStrategy);
        assertEquals(Optional.of(subStrategy),strategies.getStrategy(TestStrategy.class, ""));        
    }
    
    enum TestEnum{
        @UseStrategy(SubTestStrategyImpl.class)
        VALUE1,
        
        VALUE2
    }
    @Test
    public void testGetStrategyWithAnnotatedElement() throws Exception{
        TestStrategyImpl strategy = new TestStrategyImpl();
        strategies.putStrategy(strategy);
        
        assertEquals(Optional.of(strategy),strategies.getStrategy(TestStrategy.class, "", TestEnum.class.getField(TestEnum.VALUE2.name())));
        assertTrue(strategies.getStrategy(TestStrategy.class, "", TestEnum.class.getField(TestEnum.VALUE1.name())).get() instanceof SubTestStrategyImpl);
    }
}
