package com.github.ruediste.rise.nonReloadable.lambda;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpressionTest.SerializableFunction;

@RunWith(LambdaRunner.class)
public class MethodAndFieldReferencesTest {

    private static <T> T ensureCaptured(@Capture T object) {
        return object;
    }

    static void methodNoArgs() {

    }

    void instanceMethodNoArgs() {

    }

    static void methodWithArgs(int arg) {

    }

    void instanceMethodWithArgs(int arg) {

    }

    static String staticField;
    String instanceField;

    @Test
    public void staticMethod() {
        Runnable lambda = ensureCaptured(MethodAndFieldReferencesTest::methodNoArgs);
        LambdaExpression<?> exp = LambdaExpression.parse(lambda);
        assertEquals("()->{com.github.ruediste.rise.nonReloadable.lambda.MethodAndFieldReferencesTest.methodNoArgs()}",
                exp.toString());
    }

    @Test
    public void testInstanceMethodNoArgs() {
        Runnable lambda = ensureCaptured(this::instanceMethodNoArgs);
        LambdaExpression<?> exp = LambdaExpression.parse(lambda);
        assertEquals("()->{this.instanceMethodNoArgs()}", exp.toString());
    }

    @Test
    public void staticMethodWithArgs() {
        Consumer<Integer> lambda = ensureCaptured(MethodAndFieldReferencesTest::methodWithArgs);
        LambdaExpression<?> exp = LambdaExpression.parse(lambda);
        assertEquals(
                "(java.lang.Object P0)->{com.github.ruediste.rise.nonReloadable.lambda.MethodAndFieldReferencesTest.methodWithArgs(P0)}",
                exp.toString());
    }

    @Test
    public void testMethodRef() throws Throwable {
        SerializableFunction<Customer, Integer> pp = ensureCaptured(Customer::getData);

        LambdaExpression<Function<Customer, Integer>> parsed = LambdaExpression.parse(pp);
        Function<Object[], ?> le = parsed.compile();

        Customer c = new Customer(5);

        assertEquals(pp.apply(c), le.apply(new Object[] { c }));

        pp = ensureCaptured((Customer c1) -> c1.getData());

        parsed = LambdaExpression.parse(pp);
        le = parsed.compile();

        assertEquals(pp.apply(c), le.apply(new Object[] { c }));

        Fluent<Customer> f = new Fluent<Customer>();
        f.property(Customer::getData);

        assertEquals("public int com.github.ruediste.rise.nonReloadable.lambda.Customer.getData()", f.getMember());

        le = f.getParsed().compile();

        assertEquals(pp.apply(c), le.apply(new Object[] { c }));
    }
}
