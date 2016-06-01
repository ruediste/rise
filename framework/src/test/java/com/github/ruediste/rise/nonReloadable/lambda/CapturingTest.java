package com.github.ruediste.rise.nonReloadable.lambda;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;

@RunWith(LambdaRunner.class)
public class CapturingTest {

	<T> T capture(@Capture T obj) {
		return obj;
	}

	@Test
	public void testNoCapture() {
		Supplier<String> lambda = capture(() -> "foo");
		assertEquals("()->{foo}", LambdaExpression.parse(lambda).toString());
	}

	@Test
	public void testCaptureLocal() {
		String s = "foo";
		Supplier<String> lambda = capture(() -> s);
		assertEquals("()->{A0}", LambdaExpression.parse(lambda).toString());
	}

	String field = "bar";

	@Test
	public void testCaptureField() {
		Supplier<String> lambda = capture(() -> field);
		assertEquals("()->{this.field}", LambdaExpression.parse(lambda).toString());
	}

	String foo() {
		return "bar";
	}

	@Test
	public void testCaptureThisForMethodInvocatoin() {
		Supplier<String> lambda = capture(() -> foo());
		assertEquals("()->{this.foo()}", LambdaExpression.parse(lambda).toString());
	}
	
	@Test
	public void testPassInstanceAsParamter() {
		Function<CapturingTest,String> lambda = capture(x->x.foo());
		assertEquals("(java.lang.Object P0)->{P0.foo()}", LambdaExpression.parse(lambda).toString());
	}
	
	@Test
	public void testPassInstanceAsParamterForField() {
		Function<CapturingTest,String> lambda = capture(x->x.field);
		assertEquals("(java.lang.Object P0)->{P0.field}", LambdaExpression.parse(lambda).toString());
	}
}
