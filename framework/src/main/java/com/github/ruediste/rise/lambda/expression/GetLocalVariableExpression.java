package com.github.ruediste.rise.lambda.expression;

public class GetLocalVariableExpression extends Expression {

	private int index;

	protected GetLocalVariableExpression(Class<?> resultType, int index) {
		super(resultType);
		this.index = index;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetLocalVariableExpression other = (GetLocalVariableExpression) obj;
		if (index != other.index)
			return false;
		return true;
	}

	public int getIndex() {
		return index;
	}

}
