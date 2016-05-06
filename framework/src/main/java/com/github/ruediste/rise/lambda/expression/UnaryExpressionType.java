package com.github.ruediste.rise.lambda.expression;

public enum UnaryExpressionType {

	ArrayLength("(lenght)"), Convert("()"), Negate("-"), BitwiseNot("~"), LogicalNot("!"), IsNull("(Is Null)");
	private final String operator;

	private UnaryExpressionType(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return operator;
	}
}
