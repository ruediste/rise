/*
 * Copyright TrigerSoft <kostat@trigersoft.com> 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.ruediste.rise.nonReloadable.lambda.expression;

import java.util.Objects;

/**
 * Represents an expression that has a binary operator.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
public final class TenaryExpression extends Expression {

	private final Expression _first;
	private final Expression _second;
	private final Expression _third;
	private final TenaryExpressionType expressionType;

	TenaryExpression(TenaryExpressionType expressionType, Class<?> resultType, Expression first, Expression second, Expression _third) {
		super(resultType);
		this.expressionType = expressionType;
		_first = first;
		_second = second;
		this._third = _third;
	}

	/**
	 * Gets the second operand of the binary operation.
	 * 
	 * @return An Expression that represents the second operand of the binary
	 *         operation.
	 */
	public Expression getSecond() {
		return _second;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());
		result = prime * result + ((_first == null) ? 0 : _first.hashCode());
		result = prime * result + ((_third == null) ? 0 : _third.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TenaryExpression))
			return false;
		final TenaryExpression other = (TenaryExpression) obj;
		return Objects.equals(_first,other._first) && Objects.equals(_second, other._second);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('(');
		b.append(_first.toString());
		b.append(' ');
		b.append(expressionType);
		b.append(' ');
		b.append(_second.toString());
		b.append(' ');
		b.append(_third.toString());
		b.append(')');
		return b.toString();
	}

	public Expression getFirst() {
		return _first;
	}

	public Expression getThird() {
		return _third;
	}

	public TenaryExpressionType getExpressionType() {
		return expressionType;
	}

}
