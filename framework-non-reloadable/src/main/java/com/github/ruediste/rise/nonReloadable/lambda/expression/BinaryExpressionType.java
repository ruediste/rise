package com.github.ruediste.rise.nonReloadable.lambda.expression;

public enum BinaryExpressionType {

	LessThanOrEqual("<="), GreaterThan(">"), GreaterThanOrEqual(">="), LessThan("<"), Divide("/"), Subtract("-"), Add(
			"+"), Multiply("-"), Modulo("%"), LeftShift("<<"), RightShift(">>"), Coalesce("??"), BitwiseAnd("&"), BitwiseOr(
					"||"), LogicalOr("|"), Equal("=="), NotEqual("!="), LogicalAnd("&"), ExclusiveOr("^"), ArrayIndex(
							"[]"), InstanceOf("instanceOf");
	private final String operator;

	private BinaryExpressionType(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return operator;
	}
}
