package com.github.ruediste.rise.nonReloadable.lambda;

import java.io.Serializable;
import java.util.function.Function;

import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.Expression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.MemberExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.UnaryExpression;

public class Fluent<T> {

	public static interface Property<T, R> extends Function<T, R>, Serializable {

	}

	private LambdaExpression<Function<T, ?>> parsed;
	private String member;

	public Fluent<T> property(@Capture Property<T, ?> propertyRef) {
		LambdaExpression<Function<T, ?>> parsed = LambdaExpression
				.parse(propertyRef);
		Expression body = parsed.getBody();
		Expression method = body;
		while (method instanceof UnaryExpression)
			method = ((UnaryExpression) method).getFirst();

		member = ((MemberExpression) method).getMember().toString();
		this.parsed = parsed;
		return this;
	}

	public LambdaExpression<Function<T, ?>> getParsed() {
		return parsed;
	}

	public String getMember() {
		return member;
	}
}
