package com.github.ruediste.rise.lambda.expression;

import com.github.ruediste.rise.lambda.LambdaExpression;

/**
 * Represents a captured argument in the body of a {@link LambdaExpression}.
 */
public class CapturedArgumentExpression 
extends Expression{

	private final int index;

	protected CapturedArgumentExpression(Class<?> resultType, int index) {
		super(resultType);
		this.index = index;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		result = prime * result + 9328;
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
		CapturedArgumentExpression other = (CapturedArgumentExpression) obj;
		if (index != other.index)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "A"+index;
	}
	
	
}
